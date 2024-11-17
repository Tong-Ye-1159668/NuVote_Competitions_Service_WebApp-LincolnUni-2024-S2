import os
import re

from flask import abort, redirect, request, flash, render_template, g, jsonify, current_app as app, url_for

from voting import config
from voting.decorators import login_required, owner_required
from voting.response import Response
from voting.services import verify_site_admin_or_helper
from voting.utility import allowed_file, get_hashed_filename, read_file_extension
from .models import get_user_by_email, get_user_by_id, is_user_password_valid_by_id, update_profile_image, update_user, update_user_password_by_id,get_location_by_name,create_location
from . import bp
from ..donation.models import get_all_my_donations
from ..ticket.models import get_all_my_tickets


@bp.route('/<int:user_id>', methods=['GET', 'POST'])
@login_required
def profile(user_id):
    """edit or view user profile"""
    user = get_user_by_id(user_id)
    if not user:
        raise ValueError(r'User(user_id={user_id}) not found')
    if request.method == 'POST':
        # Get form data
        email = request.form['email']
        first_name = request.form['first_name']
        last_name = request.form['last_name']
        location_name = request.form['location']
        latitude = request.form['latitude']
        longitude = request.form['longitude']
        display_location = int(request.form.get('display_location', '0'))
        user_city_name = request.form['user_city_name']
        user_city_latitude = request.form['user_city_latitude']
        user_city_longitude = request.form['user_city_longitude']       
        if latitude == 'None' and longitude == 'None' and location_name == 'None':
            user['location_id'] = None
        
        description = request.form['description']
        editable = user['user_id'] == g.user['user_id'] or g.user['role'] in ['siteHelper', 'siteAdmin']

        # Validate email format
        if email != user['email']:
            if not re.match(config.EMAIL_REGEX, email):
                flash("Invalid email format", 'danger')
                return render_template('user/profile.html', user=user, editable=editable)

            exist_user = get_user_by_email(email)
            if exist_user and exist_user['user_id'] != user_id:
                flash("This email already exists.", 'danger')
                return render_template('user/profile.html', user=user, editable=editable)

            user['email'] = email
        # User can only modify self infomations
        if g.user['user_id'] == user['user_id']:
            user['first_name'] = first_name
            user['last_name'] = last_name
            
            user['description'] = description
             # Check if the location already exists
            if latitude != 'None' and longitude != 'None' and location_name != 'None':
                
                location = get_location_by_name(location_name, latitude, longitude)
                if not location:
                    # Insert new location
                    location_id = create_location(location_name, latitude, longitude)
                else:
                    location_id = location['location_id']
                # Update user's location_id
                user['location_id'] = location_id
                user['location'] = location_name
                user['latitude'] = latitude
                user['longitude'] = longitude
                user['display_location'] = display_location
                user['user_city_name'] = user_city_name
                user['user_city_latitude'] = user_city_latitude
                user['user_city_longitude'] = user_city_longitude
            else:
                location_id = None
                user['location_id'] = location_id
            

        # Only admin and helper can change role and status, and can not change himself role and status
        if g.user['role'] in ['siteHelper', 'siteAdmin'] and user['user_id'] != g.user['user_id']:
            # Role for voter can not be changed
            if user['role'] != 'voter':
                user['role'] = request.form['role']
            user['status'] = request.form['status']
        update_user(user)
        flash('Profile updated successfully!', 'success')
        return redirect(url_for('user.my_dashboard'))

    editable = user['user_id'] == g.user['user_id'] or g.user['role'] == 'siteAdmin' or g.user['role'] == 'siteHelper'
    # helper can not manage other helpers
    if user['role'] == 'siteHelper' and g.user['role'] == 'siteHelper' and user['user_id'] != g.user['user_id']:
        editable = False
    # helper can not manage admin user
    if user['role'] == 'siteAdmin' and g.user['role'] == 'siteHelper':
        editable = False

    
    return render_template('user/profile.html', user=user, editable=editable)

@bp.route('/<int:user_id>/public', methods=['GET'])
@login_required
def public_profile(user_id):
    '''View public profile'''
    user = get_user_by_id(user_id)
    print(user)
    if not user:
        abort(404, description=f"User(user_id={user_id}) not found")
    return render_template('user/profile_public.html', user=user)

@bp.route('/user/profile_image', methods=['POST'])
@login_required
def upload_profile_image():
    """Upload profile image for user"""
    if 'profile_image' not in request.files:
        return jsonify(success=False, error='No file part')

    file = request.files['profile_image']
    if file.filename == '':
        return jsonify(success=False, error='No selected file')

    if file and allowed_file(file.filename):
        # Hash file with md5 to generate unieq filename, avoding filename conflict
        filename = get_hashed_filename(file, read_file_extension(file.filename))
        file_path = os.path.join(app.config['PROFILE_IMAGES_ABS_PATH'], filename)
        if os.path.exists(file_path):
            file.close()
        else:
            file.save(file_path)
        file_url = f'/{config.DEFAULT_PROFILE_IMAGES_FOLDER}/{filename}'
        update_profile_image(g.user['user_id'], file_url)
        return jsonify(success=True, profile_image=file_url)

    return jsonify(success=False, error='Image format is not allowed')


@bp.route('/user/profile_image', methods=['DELETE'])
@login_required
def delete_profile_image():
    """Delete profile image for user"""
    profile_image = config.DEFAULT_PROFILE_IMAGE
    update_profile_image(
        g.user['user_id'], profile_image)
    return jsonify(success=True, profile_image=profile_image)


@bp.route('/profile/<int:user_id>/password', methods=['GET', 'POST'])
@login_required
@owner_required
def password(user_id):
    '''Handle password update'''
    if request.method == 'POST':
        old_password = request.form.get('old_password')
        new_password = request.form.get('new_password')
        confirm_new_password = request.form.get('confirm_new_password')
        # Validate passwords
        if new_password != confirm_new_password:
            flash("Passwords do not match", "danger")
            return redirect(url_for('user.password', user_id=user_id))
        if old_password == new_password:
            flash("New Password cannot be same as the old password", "danger")
            return redirect(url_for('user.password', user_id=user_id))
        if not re.match(config.DEFAULT_PASSWORD_REGEX, new_password):
            flash('Password must be at least 8 characters long and include a mix of letters, numbers, '
                  'and special characters (@$!%*?&)!', 'danger')
            return redirect(url_for('user.password', user_id=user_id))
        if not is_user_password_valid_by_id(user_id, old_password):
            flash('Password is incorrect!', 'danger')
            return redirect(url_for('user.password', user_id=user_id))
        # Update password if old password is valid
        else:
            update_user_password_by_id(user_id, new_password)
            flash("Password updated successfully", "success")
            return redirect(url_for('user.profile', user_id=user_id))
    # Render the password change page
    return render_template('user/password.html', user=get_user_by_id(user_id), mode='view')


@bp.route('/dashboard', methods=['GET'])
@login_required
def my_dashboard():
    return build_dashboard_view(g.user)

@bp.route('/dashboard/<int:user_id>', methods=['GET'])
@login_required
def dashboard(user_id):
    user = get_user_by_id(user_id)
    if not user:
        abort(404, description=f"User(user_id={user_id}) not found")
    if not (verify_site_admin_or_helper(user_id) or user_id == g.user['user_id']):
        abort(403, description="You are not authorized to access this page")
    return build_dashboard_view(user)


@bp.route('/<int:user_id>/details', methods=['GET'])
@login_required
def get_user_details(user_id):
    if not (verify_site_admin_or_helper(user_id) or user_id == g.user['user_id']):
        abort(403, description="You are not authorized to access this page")
    user = get_user_by_id(user_id)
    if not user:
        return Response.error(f"User(user_id={user_id}) not found")
    return Response.success('User details fetched successfully', user)

def build_dashboard_view(user):
    return render_template('user/dashboard.html', user=user, tickets=get_all_my_tickets(user['user_id']), donations=get_all_my_donations(user['user_id']))
