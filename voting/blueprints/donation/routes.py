import io
import os
import re
from datetime import datetime

from flask import render_template, redirect, abort, url_for, flash, request, jsonify, g, current_app as app, \
    make_response
from xhtml2pdf import pisa

from voting import config
from voting.blueprints.theme.models import get_theme_by_id
from voting.decorators import login_required, theme_admin_required, admin_required, json_response
from voting.services import verify_theme_admin
from voting.utility import allowed_file, get_hashed_filename, read_file_extension
from voting.utility import are_fields_present
from voting.response import Response
from . import bp
from .models import (
    Charity,
    get_charity_by_id,
    create_charity,
    update_charity,
    update_charity_approved,
    get_all_charities,
    Donation,
    create_donation,
    get_donation_by_id,
    get_all_my_donations,
    get_total_donations_for_charity,
    get_charities_by_theme,
    get_donations_by_charity,
    get_total_donations_by_period,
    search_donors
)
from ...signals import charity_approved, charity_declined, charity_submitted


# Create new charity/donation
@bp.route('/charity_create', methods=['GET', 'POST'])
@login_required
def charity_create():
    '''render the donation create page'''
    if request.method == 'POST':
        if are_fields_present(request, ['charity_name', 'charity_description', 'reg_num',
                                        'bank_acc', 'ird_num', 'theme_id']):
            charity_name = request.form['charity_name']
            charity_description = request.form['charity_description']
            reg_num = request.form['reg_num']
            bank_acc = request.form['bank_acc']
            ird_num = request.form['ird_num']
            theme_id = request.form['theme_id']

            # Get theme by id
            theme = get_theme_by_id(theme_id)

            # Check if theme exists
            if not theme:
                abort(404, description=f"Theme with id {theme_id} not found")

            # Check if user is a theme admin
            if not verify_theme_admin(theme_id):
                abort(403, description='Only theme admins can access this page.')

            # Server-side validation
            if not verify_charity(charity_name, charity_description, reg_num, bank_acc, ird_num):
                return redirect(url_for('donation.charity_create', theme_id=theme_id))

            # Check if the post request has the file part
            if 'charity_image' not in request.files:
                return jsonify(success=False, error='No file part')
            file = request.files['charity_image']
            if file.filename == '':
                return jsonify(success=False, error='No selected file')
            if file and allowed_file(file.filename):
                # Hash file with md5 to generate unique filename, avoiding filename conflict
                filename = get_hashed_filename(file, read_file_extension(file.filename))
                file_path = os.path.join(app.config['CHARITY_IMAGES_ABS_PATH'], filename)
                if os.path.exists(file_path):
                    file.close()
                else:
                    file.save(file_path)
                file_url = f'/{config.DEFAULT_CHARITY_IMAGES_FOLDER}/{filename}'

                # Create charity object and save to database
                charity = Charity(charity_name, charity_description, file_url, reg_num, bank_acc,
                                  ird_num, g.user['user_id'], theme_id)
                charity_id = create_charity(charity)

                # Send notification to site admin
                charity_submitted_notification(charity_id)
                flash("Charity created successfully", "success")

                # Redirect to the charity view page
                return redirect(url_for('donation.charity_view', charity_id=charity_id, theme_id=theme_id))
        
        # If the form data is invalid, redirect back to the charity create page
        flash('Input is not valid', 'danger')
        return redirect(url_for('donation.charity_create'))
    
    # Render the charity create page on GET request
    theme_id = request.args.get('theme_id')
    theme = get_theme_by_id(theme_id)
    if not theme:
        abort(404, description=f"Theme with id {theme_id} not found")
    return render_template('donations/charity_create.html', theme=theme)


# View charity information
@bp.route('/charity/<int:charity_id>')
# @login_required
def charity_view(charity_id):
    """Return the charity view page"""

    # Get charity by id
    charity = get_charity_by_id(charity_id)

    # Check if charity exists
    if not charity:
        abort(404, "charity not found!")

    # Get status message for the charity
    (approved, status_message) = get_charity_status_and_message(charity)

    # Get total donations for the charity
    total_donations = get_total_donations_for_charity(charity_id)

    # Render the charity view page
    return render_template('donations/charity_view.html', charity=charity,
                           charity_id=charity_id, approved=approved, status_message=status_message,
                           total_donations=total_donations)


# Edit charity information
@bp.route('/<int:theme_id>/charity/edit/<int:charity_id>', methods=['GET', 'POST'])
@theme_admin_required
def charity_edit(theme_id, charity_id):
    '''render the charity information edit page'''
    charity = get_charity_by_id(charity_id)

    # Check if user is the creator of the charity
    if g.user['user_id'] != charity['create_by']:
        abort(403, "You don't have permission to access this resource.")

    if request.method == 'POST':

        # Check if the charity exists
        if not charity:
            abort(404, "charity not found!")

        # Check if the post request has the file part
        if are_fields_present(request, ['charity_name', 'charity_description', 'reg_num',
                                        'bank_acc', 'ird_num']):
            charity_name = request.form['charity_name']
            charity_description = request.form['charity_description']
            reg_num = request.form['reg_num']
            bank_acc = request.form['bank_acc']
            ird_num = request.form['ird_num']

            # Server-side validation
            if not verify_charity(charity_name, charity_description, reg_num, bank_acc, ird_num):
                return redirect(url_for('donation.charity_edit', charity_id=charity_id, theme_id=theme_id))
            
            # Create new charity object and update the charity
            new_charity = Charity(charity_name, charity_description, charity['charity_image'], reg_num, bank_acc,
                                  ird_num,
                                  charity['create_by'], charity['theme_id'], charity['charity_id'])
            update_charity(charity_id, new_charity)
            flash("Charity updated successfully", "success")
        return redirect(
            url_for('donation.charity_view', charity_id=charity['charity_id'], theme_id=charity['theme_id']))
    else:
        charity = get_charity_by_id(charity_id)

        # Check if the charity exists
        if not charity:
            abort(404, "charity not found!")
        return render_template('donations/charity_edit.html', charity=charity)


# Theme admin manage charities
@bp.route('/charity_tAdmin_mgmt/<int:theme_id>', methods=['GET'])
@theme_admin_required
def charity_tAdmin_mgmt(theme_id):
    '''render the theme admin manage charities' application page'''

    # Get charities by theme
    charities = get_charities_by_theme(theme_id)
    return render_template('donations/charity_tAdmin_mgmt.html', charities=charities, theme_id=theme_id)


# Site admin manage charities
@bp.route('/charity_sAdmin_mgmt', methods=['GET'])
@admin_required
def charity_sAdmin_mgmt():
    '''render the site admin manage charities' information and report page'''

    # Get all charities
    charities = get_all_charities()

    # Reverse the list so that the latest charity is shown first
    charities.reverse()
    return render_template('donations/charity_sAdmin_mgmt.html', charities=charities)


# approve or decline charity
@bp.route('/charity/<int:charity_id>', methods=['POST'])
@admin_required
def charity_resolution(charity_id):
    '''Stie Admin to Approve or decline a charity'''

    charity = get_charity_by_id(charity_id)
    if not charity:
        abort(404, "charity not found!")
    if are_fields_present(request, ['resolution']):
        resolution = request.form['resolution']
        
        # Approve the charity application
        if resolution == '1':
            update_charity_approved(charity_id, 1, g.user['user_id'])
            flash("Charity approved successfully", "success")
            charity_approved_notification(charity)

        # Decline the charity application
        elif resolution == '0':
            update_charity_approved(charity_id, 0, g.user['user_id'])
            flash("Charity declined successfully", "success")
            charity_declined_notification(charity)
        else:
            flash("Invalid input", "danger")
    return redirect(url_for('donation.charity_sAdmin_mgmt'))


@bp.route('/donate/<int:charity_id>', methods=['GET', 'POST'])
@login_required
def donate(charity_id):
    '''Render the donate page and handle donations'''
    charity = get_charity_by_id(charity_id)
    if not charity:
        abort(404, "Charity not found!")

    if request.method == 'POST':
        # Extract form data (the amount input is already a number, no need for float conversion)
        try:
            amount = float(request.form.get('amount'))  # Convert to float, just in case it's sent incorrectly
        except ValueError:
            flash('Invalid donation amount. Please enter a valid number.', 'danger')
            return redirect(url_for('donation.donate', charity_id=charity_id))

        card_num = request.form.get('card_num')
        expiration_date = request.form.get('expiration_date')
        cvv = request.form.get('cvv')
        message = request.form.get('message', '')

        # Server-side validation
        if not verify_donation(amount, card_num, expiration_date, cvv, message):
            # Validation failed, redirect back to the donation form with an error message
            return redirect(url_for('donation.donate', charity_id=charity_id))

        # All validations passed, create the donation object
        try:
            donation = Donation(amount, card_num, message, g.user['user_id'], charity_id)
            donation_id = create_donation(donation)
            flash("You have donated successfully. Thank you for your donation.", "success")
            return redirect(url_for('donation.donate_success', donation_id=donation_id))

        except Exception as e:
            # Log the exception for debugging purposes
            app.logger.error(f"Error processing donation: {e}")
            flash("An error occurred while processing your donation. Please try again.", "danger")
            return redirect(url_for('donation.donate', charity_id=charity_id))

    # Total donations for the charity
    total_donations = get_total_donations_for_charity(charity_id)
    return render_template('donations/donate_create.html', charity=charity, total_donations=total_donations)


@bp.route('/donate_success/<int:donation_id>')
@login_required
def donate_success(donation_id):
    '''render the donate success and receipt page'''
    donation = get_donation_by_id(donation_id)
    charity_id = donation['charity_id']
    charity = get_charity_by_id(charity_id)

    # Mask the card number before passing it to the template
    donation['card_num'] = mask_card_number(donation['card_num'])

    # Check if the logged-in user is the donor of the donation
    if donation['donated_by'] != g.user['user_id']:
        abort(403, description='You are not authorized to view this donation')

    return render_template('donations/donate_success.html', donation=donation, charity=charity)


@bp.route('donation/pdf/<int:donation_id>')
@login_required
def generate_pdf(donation_id):
    '''Generate a PDF receipt for a donation'''
    donation = get_donation_by_id(donation_id)
    charity_id = donation['charity_id']
    charity = get_charity_by_id(charity_id)

    # Mask the card number before passing it to the template
    donation['card_num'] = mask_card_number(donation['card_num'])

    # Combine charity and donation data in a context dictionary
    context = {
        'charity': charity,
        'donation': donation
    }
    # Generate the PDF
    pdf = html_to_pdf('donations/receipt.html', context)

    if pdf:
        response = make_response(pdf.read())
        response.headers['Content-Type'] = 'application/pdf'
        response.headers['Content-Disposition'] = f'inline; filename="receipt_{donation_id}.pdf"'
        return response

    return 'PDF generation failed', 500


@bp.route('/donation/history')
@login_required
def donation_history():
    """Render the donation history page for a voter."""
    if g.user['role'] != 'voter':
        abort(403, description="Only voters can access their donation history.")

    # Fetch donations made by the logged-in user
    donations = get_all_my_donations(g.user['user_id'])
    return render_template('donations/donation_history.html', donations=donations)


@bp.route('/<int:theme_id>/charity/report/<int:charity_id>')
@theme_admin_required
def charity_report(charity_id, theme_id):
    '''Render the charity/donations report page for theme admins'''
    charity = get_charity_by_id(charity_id)

    # Check if is a valid charity
    if not charity or charity['approved'] != 1:
        abort(403, "You can only view reports for approved charities.")

    # Retrieve donations and totals grouped by year, month, and day
    donations = get_donations_by_charity(charity_id)
    total_by_year = get_total_donations_by_period(charity_id, 'YEAR')
    total_by_month = get_total_donations_by_period(charity_id, 'MONTH')
    total_by_day = get_total_donations_by_period(charity_id, 'DAY')

    # Get the current year and month
    current_year = datetime.now().year
    current_month = datetime.now().strftime('%B')  # Full month name

    return render_template(
        'donations/charity_report.html',
        charity=charity,
        donations=donations,
        total_by_year=total_by_year,
        total_by_month=total_by_month,
        total_by_day=total_by_day,
        theme_id=theme_id,
        current_year=current_year,
        current_month=current_month
    )

@bp.route('/search_donor', methods=['GET'])
@json_response
def search_donor():
    '''Search for donors by first or last name'''
    query = request.args.get('query', '')
    donors = search_donors(query)
    return Response.success('Donors fetched successfully', donors)


def verify_charity(charity_name, charity_description, reg_num, bank_acc, ird_num):
    '''Verify the charity form'''
    # Server-side validation
    if len(charity_name) == 0:
        flash('Charity name is required.', 'danger')
        return False

    # Validate charity description length (should be at most 255 characters)
    if len(charity_description) > 255:
        flash('Charity description is too long. Please limit it to 255 characters.', 'danger')
        return False

    # Validate reg_num format (2-3 letters followed by 4-6 digits)
    reg_num_pattern = r'^[A-Za-z]{2,3}\d{4,6}$'
    if not re.match(reg_num_pattern, reg_num):
        flash('Registration number must be 2-3 letters followed by 4-6 digits.', 'danger')
        return False

    # Validate bank account format (XX-XXXX-XXXXXXX-XX or XX-XXXX-XXXXXXX-XXX)
    bank_acc_pattern = r'^\d{2}-\d{4}-\d{7}-\d{2,3}$'
    if not re.match(bank_acc_pattern, bank_acc):
        flash('Bank account number must be in the format XX-XXXX-XXXXXXX-XX or XX-XXXX-XXXXXXX-XXX.', 'danger')
        return False

    # Validate IRD number (XXX-XXX-XXX format with exactly 9 digits)
    ird_num_pattern = r'^\d{3}-\d{3}-\d{3}$'
    if not re.match(ird_num_pattern, ird_num):
        flash('IRD number must be in the format XXX-XXX-XXX.', 'danger')
        return False

    return True


def get_charity_status_and_message(charity):
    '''Show status message for the charity'''
    if charity['approved'] == 1:
        return f"Status: Approved", "This charity and donation activity has been approved!"
    elif charity['approved'] == 0:
        return f"Status: Declined", "This charity and donation activity has been declined."
    return f"Status: Pending", "This charity is still under review by the site admin."


def charity_submitted_notification(charity_id):
    '''Send notification to site admin when a charity is submitted'''
    charity = get_charity_by_id(charity_id)
    content = f'Charity "{charity["charity_name"]}" has been submitted, please approve it in time.'
    url = url_for('donation.charity_sAdmin_mgmt', _anchor=f'charity-{charity["charity_id"]}')
    charity_submitted.send(charity, content=content, url=url)


def charity_approved_notification(charity):
    '''Send notification to the charity creator when a charity is approved'''
    content = f'Congratulations! Charity "{charity["charity_name"]}" has been approved.'
    url = url_for('donation.charity_tAdmin_mgmt', theme_id=charity['theme_id'])
    charity_approved.send(charity, content=content, url=url)


def charity_declined_notification(charity):
    '''Send notification to the charity creator when a charity is declined'''
    content = f'Sorry. Charity "{charity["charity_name"]}" has been declined.'
    url = url_for('donation.charity_tAdmin_mgmt', theme_id=charity['theme_id'])
    charity_declined.send(charity, content=content, url=url)


def html_to_pdf(template_src, context_dict):
    """Convert HTML template to PDF."""
    template = render_template(template_src, **context_dict)
    pdf = io.BytesIO()
    pisa_status = pisa.CreatePDF(io.StringIO(template), dest=pdf)
    if pisa_status.err:
        return None
    pdf.seek(0)
    return pdf


def mask_card_number(card_num):
    '''Mask the donor's card number for security purposes.'''
    # Show the first 4 digits, mask the middle 8 digits, and show the last 4 digits
    return card_num[:4] + " **** **** " + card_num[-4:]


def verify_donation(amount, card_num, expiration_date, cvv, message):
    """Server-side validation for donations."""
    # Validate amount (should be positive and at least 1)
    if amount < 1:
        flash('Amount must be at least $1.', 'danger')
        return False

    # Validate card number (using a regex pattern)
    card_pattern = r"^\d{4} \d{4} \d{4} \d{4}$"
    if not re.match(card_pattern, card_num):
        flash('Invalid card number format. Expected 16-digit format: xxxx xxxx xxxx xxxx.', 'danger')
        return False

    # Validate expiration date (should not be in the past)
    try:
        exp_month, exp_year = expiration_date.split('/')
        exp_year = '20' + exp_year  # Convert YY to YYYY
        exp_date = datetime(int(exp_year), int(exp_month), 1)

        # Get current month and year, but set day as 1 for comparison purposes
        now = datetime.now()
        current_date = datetime(now.year, now.month, 1)

        # If expiration date is before the current month/year, it's invalid
        if exp_date < current_date:
            flash('Expiration date cannot be in the past.', 'danger')
            return False
    except ValueError:
        flash('Invalid expiration date format. Expected format: MM/YY.', 'danger')
        return False

    # Validate CVV (should be 3 digits)
    if not (len(cvv) in [3] and cvv.isdigit()):
        flash('Invalid CVV. Please enter a 3-digit CVV code.', 'danger')
        return False

    # Validate message length (should be at most 255 characters)
    if len(message) > 255:
        flash('Message is too long. Please limit it to 255 characters.', 'danger')
        return False

    return True
