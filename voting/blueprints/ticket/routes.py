# ENGAGING WITH COMPETITIONS
from flask import render_template, redirect, url_for, request, g, flash, abort

from voting.blueprints.event.models import get_event_by_id
from voting.config import DEFAULT_TICKET_STATUS
from voting.decorators import login_required, site_role_required, owner_required
from .models import Ticket, create_ticket, get_ticket_by_id, create_comment, \
    Comment, get_comments_by_ticket_id, get_open_tickets, get_closed_tickets, get_new_tickets, \
    assign_ticket, ticket_closed_notification, update_ticket_solution, ticket_created_notification, \
    ticket_commented_notification, \
    ticket_assigned_notification, unassign_ticket
from voting.utility import are_fields_present, get_current_datetime
from . import bp
from ..canditate.models import get_candidate_by_id
from ..theme.models import get_theme_by_id
from ..user.models import get_power_user


@bp.route('/create', methods=['GET', 'POST'])
@login_required
def ticket_create():
    """Render the ticket create page"""
    if request.method == 'POST':
        if are_fields_present(request, ['subject', 'content']):
            ticket = Ticket(request.form['subject'], request.form['content'],
                            DEFAULT_TICKET_STATUS, g.user['user_id'], get_current_datetime())
            ticket = create_ticket(ticket)
            ticket_created_notification(ticket)
            flash("Ticket has created successfully", "success")
        return redirect(url_for('user.my_dashboard') + '#tickets')
    return render_template('ticket_create.html')


@bp.route('/report/<int:candidate_id>', methods=['GET'])
@login_required
def report_an_invalid_object(candidate_id):
    """Render the ticket create page"""
    candidate = get_candidate_by_id(candidate_id)
    event = get_event_by_id(candidate['event_id'])
    form = {'subject': f'Report a suspicious vote',
            'content': f'The candidate ({candidate["name"]}) in event ({event["name"]}) may have some suspicious votes'}
    return render_template('ticket_create.html', form=form)


@bp.route('/<int:ticket_id>')
@login_required
def ticket_by_id(ticket_id):
    """Render the ticket details page"""
    ticket = get_ticket_by_id(ticket_id)
    if ticket['created_by'] != g.user['user_id'] and g.user['role'] not in ['siteHelper', 'siteAdmin']:
        abort(403, description='You are not authorized to view this ticket')

    return render_template('ticket_view.html', ticket=ticket,
                           replies=get_comments_by_ticket_id(ticket_id))


@bp.route('/<int:ticket_id>/reply', methods=['POST'])
@login_required
def reply_ticket_by_id(ticket_id):
    """Create a comment"""
    if request.method == 'POST':
        if are_fields_present(request, ['content']):
            comment = Comment(ticket_id, request.form['content'], g.user['user_id'])
            create_comment(comment)
            ticket_commented_notification(ticket_id)
            flash("Comment has created successfully", "success")
    return redirect(url_for('ticket.ticket_by_id', ticket_id=ticket_id))


# Render the admin home page with user information
@bp.route('/mgmt')
@site_role_required
def tickets_mgmt():
    """Render the tickets management page"""
    new_tickets = get_new_tickets()
    wip_tickets = get_open_tickets()
    done_tickets = get_closed_tickets()

    return render_template('tickets_mgmt.html', new_tickets=new_tickets, wip_tickets=wip_tickets,
                           done_tickets=done_tickets)


@bp.route('/<int:ticket_id>/cancel', methods=['POST'])
@login_required
def cancel_ticket_by_id(ticket_id):
    """Cancel ticket"""
    if request.method == 'POST':
        ticket = get_ticket_by_id(ticket_id)
        if ticket['created_by'] != g.user['user_id']:
            abort(403, description='You are not authorized to modify this ticket')
        update_ticket_solution(ticket_id, g.user['user_id'], get_current_datetime(), 'cancelled')
        ticket_closed_notification(ticket_id)
        flash("Ticket has cancelled successfully", "success")
    return redirect(url_for('user.my_dashboard') + '#tickets')


@bp.route('/update/<int:ticket_id>', methods=['GET', 'POST'])
@site_role_required
def ticket_update_by_id(ticket_id):
    """Render the ticket create page"""
    if request.method == 'POST':
        if are_fields_present(request, ['assign_to']):
            assign_to_value = request.form['assign_to']
            if assign_to_value == 'unassign':
                unassign_ticket(ticket_id, g.user['user_id'], get_current_datetime())
            else:
                assign_ticket(ticket_id, g.user['user_id'], get_current_datetime(), assign_to_value)
                ticket_assigned_notification(ticket_id)
            flash("Ticket has updated successfully", "success")
            return redirect(url_for('ticket.tickets_mgmt'))
        if are_fields_present(request, ['solution']):
            update_ticket_solution(ticket_id, g.user['user_id'], get_current_datetime(), request.form['solution'])
            ticket_closed_notification(ticket_id)
            flash("Ticket has updated successfully", "success")
            return redirect(url_for('ticket.tickets_mgmt'))
    power_users = get_power_user()
    return render_template('ticket_edit.html', ticket=get_ticket_by_id(ticket_id), power_users=power_users)
