import os
from datetime import datetime

from flask import render_template, redirect, abort, url_for, flash, request, jsonify, g, current_app as app

from voting import config
from voting.blueprints.theme.models import get_theme_by_id
from voting.decorators import json_response, login_required, theme_admin_required, theme_role_required
from voting.response import Response
from voting.services import verify_theme_admin
from .models import get_all_events, get_event_by_id, delete_event, \
    Event, create_event, update_event, update_event_status, update_event_image
from voting.utility import are_fields_present
from voting.utility import allowed_file, get_hashed_filename, read_file_extension
from voting.signals import event_published, event_verified
from . import bp

@bp.route('/')
@login_required
def events_mgmt():
    '''Render the admin home page with user information'''
    events = get_all_events()
    if g.user['role'] == 'scrutineer':
        events = [c for c in events if c['status'] != 'draft']
    events.reverse()
    return render_template('events/events_mgmt.html', events=events)


@bp.route('/create', methods=['GET', 'POST'])
@login_required
def event_create():
    '''Render the event create page'''
    if request.method == 'POST':
        if are_fields_present(request, ['name', 'description', 'start_date', 'end_date', 'theme_id']):
            name = request.form['name']
            description = request.form['description']
            start_date = request.form['start_date']
            end_date = request.form['end_date']
            theme_id = request.form['theme_id']

            theme = get_theme_by_id(theme_id)
            if not theme:
                abort(404, description=f"Theme with id {theme_id} not found")
            if not verify_theme_admin(theme_id):
                abort(403, description='Only theme admins can access this page.')

            if not verify_event(name, description, start_date, end_date):
                return redirect(url_for('event.event_create'))

            if 'image' not in request.files:
                return jsonify(success=False, error='No file part')
            file = request.files['image']
            if file.filename == '':
                return jsonify(success=False, error='No selected file')
            if file and allowed_file(file.filename):
                # Hash file with md5 to generate unique filename, avoiding filename conflict
                filename = get_hashed_filename(file, read_file_extension(file.filename))
                file_path = os.path.join(app.config['EVENT_IMAGES_ABS_PATH'], filename)
                if os.path.exists(file_path):
                    file.close()
                else:
                    file.save(file_path)
                file_url = f'/{config.DEFAULT_EVENT_IMAGES_FOLDER}/{filename}'
                event = Event(name, description, file_url, start_date, end_date,
                                          config.DEFAULT_EVENT_STATUS, g.user['user_id'], theme_id)
                create_event(event)
                return redirect(url_for('theme.manage_theme', theme_id=theme_id))
        flash('Input is not valid', 'danger')
        return redirect(url_for('event.event_create'))
    
    theme_id = request.args.get('theme_id')
    theme = get_theme_by_id(theme_id)
    if not theme:
        abort(404, description=f"Theme with id {theme_id} not found")
    return render_template('events/event_create.html', theme=theme)


@bp.route('/manage')
@theme_admin_required
def event_manage():
    '''Render the event management page'''
    return render_template('events/event_mgmt.html')


@bp.route('/edit/<int:event_id>', methods=['GET', 'POST'])
@theme_admin_required
def event_edit(event_id):
    '''Render the event edit page'''
    if request.method == 'POST':
        # verify form
        event = get_event_by_id(event_id)
        if not event:
            abort(404, description=f"Event with id {event_id} not found")
        if are_fields_present(request, ['name', 'description', 'image', 'start_date', 'end_date']):
            name = request.form['name']
            description = request.form['description']
            start_date = request.form['start_date']
            end_date = request.form['end_date']

            if not verify_event(name, description, start_date, end_date):
                return redirect(url_for('event.event_edit', event_id=event_id))
            new_event = Event(name, description, request.form['image'], start_date, end_date,
                                      event['status'], event['create_by'], event['theme_id'])
            update_event(event_id, new_event)
            flash("Event updated successfully", "success")
        return redirect(url_for('theme.manage_theme', theme_id=event['theme_id']))
    else:
        event = get_event_by_id(event_id)
        if not event:
            abort(404, description=f"Event with id {event_id} not found")
        return render_template('events/event_edit.html', event=event)


@bp.route('/delete/<int:event_id>', methods=['POST'])
@theme_admin_required
def event_delete(event_id):
    '''Delete the event'''
    event = get_event_by_id(event_id)
    if not event:
        abort(404, description=f"Event with id {event_id} not found")
    if delete_event(event_id):
        flash("Event deleted successfully", "success")
        return redirect(url_for('theme.manage_theme', theme_id=event['theme_id']))
    else:
        abort(500, description=f"Filed to delete the Event with id {event_id}!")


# @bp.route('/finished/<int:event_id>', methods=['POST'])
# @theme_role_required
# def event_finished(event_id):
#     '''Finish the event'''
#     event = get_event_by_id(event_id)
#     if event['end_date'] > datetime.now():
#         flash("Current event is not completed yet!", "danger")
#         return redirect(url_for('event.events_mgmt'))
#     if update_event_status(event_id, 'finished'):
#         flash("Event updated successfully", "success")
#         return redirect(url_for('event.events_mgmt'))
#     else:
#         abort(403, description=f"Filed to update the event with id {event_id}!")


@bp.route('/verify/<int:event_id>', methods=['POST'])
@theme_role_required
def event_verify(event_id):
    '''Approve the event'''
    event = get_event_by_id(event_id)
    if not event:
        abort(404, description=f"Event with id {event_id} not found")
    if update_event_status(event_id, 'verified'):
        event_verified_notification(event)
        flash("Event verified successfully", "success")
        return redirect(url_for('theme.manage_theme', theme_id=event['theme_id']))
    else:
        abort(500, description=f"Filed to verify the event with id {event_id}!")

@bp.route('/publish/<int:event_id>', methods=['POST'])
@theme_admin_required
@json_response
def event_publish(event_id):
    '''Publish the event'''
    event = get_event_by_id(event_id)
    if not event:
        return Response.error(f"Event with id {event_id} not found")
    if event['status'] != 'draft':
        return Response.error(f"Event with id {event_id} is not in draft status")
    if update_event_status(event_id, 'published'):
        event_published_notification(event)
        return Response.success("Event published successfully")
    else:
        return Response.error(f"Filed to publish the event with id {event_id}!")

@bp.route('/image', methods=['DELETE'])
@theme_admin_required
def delete_event_image():
    """Delete profile image for user"""
    image = config.DEFAULT_EVENT_IMAGE
    update_event_image(g.user['user_id'], image)
    return jsonify(success=True, profile_image=image)


def verify_event(name, description, start_date, end_date):
    '''Verify the event form'''
    # Convert string to datetime
    start_date_dt = datetime.fromisoformat(start_date)
    end_date_dt = datetime.fromisoformat(end_date)
    now = datetime.now()

    # Server-side validation
    if len(name) == 0:
        flash('name is required.', 'danger')
        return False
    if len(description) == 0:
        flash('description is required.', 'danger')
        return False
    if start_date_dt < now:
        flash('Start date cannot be in the past.', 'danger')
        return False
    if end_date_dt <= start_date_dt:
        flash('End date cannot be equal to or earlier than the start date.', 'danger')
        return False
    return True

def event_published_notification(event):
    content = f'New event {event["name"]} has been published.'
    url = url_for('event.event_view', event_id=event['event_id'])
    event_published.send(event, content=content, url=url)

def event_verified_notification(event):
    content = f'Result of event {event["name"]} has been verified.'
    url = url_for('event.result', event_id=event['event_id'])
    event_verified.send(event, content=content, url=url)