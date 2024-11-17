import os
from datetime import datetime

from flask import render_template, g, request, jsonify, abort, redirect, url_for, flash, current_app as app

from voting import config
from voting.services import verify_theme_admin
from ..event.models import get_event_by_id
from .models import get_candidate_by_id, \
    update_candidate, create_candidate, delete_candidate, get_location_by_name, create_location
from voting.utility import allowed_file, get_hashed_filename, read_file_extension
from . import bp


@bp.route('/<int:candidate_id>/edit', methods=['GET', 'POST'])
def candidate_edit(candidate_id):
    '''Edit a candidate'''

    # Check if candidate exists
    candidate = get_candidate_by_id(candidate_id)
    
    if not candidate:
        abort(404, description=f"Candidate with id {candidate_id} not found!")

    # Check if event exists
    event = get_event_by_id(candidate['event_id']);

    if not event:
        abort(404, description=f"Event with id {candidate['event_id']} not found!")
    if not verify_theme_admin(event['theme_id']):
        abort(403, description=f"You are not allowed to edit this candidate.")

    # Check if the event is ongoing or approved
    now = datetime.now()
    if event['status'] in ('finished', 'approved'):
        flash('You cannot edit a candidate in an approved event.', 'danger')
        return render_template('candidates/candidate_edit.html', candidate=candidate, view=True)
    elif event['start_date'] < now and event['end_date'] > now:
        flash('You cannot edit a candidate in an ongoing event.', 'danger')
        return render_template('candidates/candidate_edit.html', candidate, view=True)

    if request.method == 'POST':
        # Form submission logic
        name = request.form['name']
        description = request.form['description']
        author = request.form['author']
        image = request.files['image']
        latitude = request.form['latitude']
        longitude = request.form['longitude']
        location_name = request.form['location']
        print('location_name', location_name, latitude, longitude)
        if latitude == 'None' and longitude == 'None' and location_name == 'None':
            candidate['location_id'] = None
        if not name or not description or not author:
            flash('All fields are required.', 'danger')
            return render_template('candidates/candidate_edit.html', candidate=candidate)

        if image and image.filename != '':
            if allowed_file(image.filename):
                # Hash file with md5 to generate unieq filename, avoding filename conflict
                filename = get_hashed_filename(image, read_file_extension(image.filename))
                file_path = os.path.join(app.config['CANDIDATE_IMAGES_ABS_PATH'], filename)
                if os.path.exists(file_path):
                    image.close()
                else:
                    image.save(file_path)
                file_url = f'/{config.DEFAULT_CANDIDATE_IMAGES_FOLDER}/{filename}'
                candidate['image'] = file_url
            else:
                flash('Image format is not allowed.', 'danger')
                return render_template('candidates/candidate_edit.html', candidate=candidate)
        if latitude != 'None' and longitude != 'None' and location_name != 'None':
                
                location = get_location_by_name(location_name, latitude, longitude)
                print('location', location)
                if not location:
                    # Insert new location
                    location_id = create_location(location_name, latitude, longitude)
                else:
                    location_id = location['location_id']
                # Update user's location_id
                candidate['location_id'] = location_id
                candidate['location'] = location_name
                candidate['latitude'] = latitude
                candidate['longitude'] = longitude
        else:
                location_id = None
                candidate['location_id'] = location_id

        # Update candidate details
        candidate['name'] = name
        candidate['description'] = description
        candidate['author'] = author
        # Save candidate to the database
        update_candidate(candidate)
        flash('Candidate saved successfully.', 'success')
        return redirect(url_for('event.candidates_manage', event_id=candidate['event_id']))

    return render_template('candidates/candidate_edit.html', candidate=candidate,event=event)


@bp.route('/new', methods=['GET', 'POST'])
def candidate_new():
    '''Create a new candidate'''
    event_id = request.args.get('event_id')
    if not event_id:
        event_id = request.form['event_id']

    # Check if event exists
    event = get_event_by_id(event_id);
    if not event:
        abort(404, description=f"Event with id {event_id} not found!")
    if not verify_theme_admin(event['theme_id']):
        abort(403, description=f"You are not allowed to create a candidate for this event.")

    candidate = {'event_id': event_id}

    # Check if the event is finished or approved
    now = datetime.now()
    if event['status'] in ('approved'):
        flash('You cannot create a candidate for an approved event.', 'danger')
        return render_template('candidate_edit.html', candidate=candidate)
    elif event['start_date'] < now:
        flash('You cannot create a candidate for an started event.', 'danger')
        return render_template('candidate_edit.html', candidate=candidate)

    if request.method == 'POST':
        # Form submission logic
        name = request.form['name']
        description = request.form['description']
        author = request.form['author']
        image = request.files['image']
        latitude = request.form['latitude']
        longitude = request.form['longitude']
        location_name = request.form['location']
        print('location_name', location_name, latitude, longitude)
        

        if not name or not description or not author or not image:
            flash('All fields are required.', 'danger')
            return render_template('candidate_edit.html', candidate=candidate)

        if image and allowed_file(image.filename):
            # Hash file with md5 to generate unieq filename, avoding filename conflict
            filename = get_hashed_filename(image, read_file_extension(image.filename))
            file_path = os.path.join(app.config['CANDIDATE_IMAGES_ABS_PATH'], filename)
            if os.path.exists(file_path):
                image.close()
            else:
                image.save(file_path)
            file_url = f'/{config.DEFAULT_CANDIDATE_IMAGES_FOLDER}/{filename}'
            candidate['image'] = file_url
        else:
            flash('Image format is not allowed.', 'danger')
            return render_template('candidate_edit.html', candidate=candidate)

        if latitude != 'None' and latitude != '' and longitude != 'None' and longitude != '' and location_name != 'None' and location_name != '':
                
                location = get_location_by_name(location_name, latitude, longitude)
                print('location', location)
                if not location:
                    # Insert new location
                    location_id = create_location(location_name, latitude, longitude)
                else:
                    location_id = location['location_id']
                # Update user's location_id
                candidate['location_id'] = location_id
                candidate['location'] = location_name
                candidate['latitude'] = latitude
                candidate['longitude'] = longitude
        else:
                location_id = None
                candidate['location_id'] = location_id
        # Update candidate details
        candidate['event_id'] = event_id
        candidate['name'] = name
        candidate['description'] = description
        candidate['author'] = author
        candidate['create_by'] = g.user['user_id']

        # Save candidate to the database
        create_candidate(candidate)
        flash('Candidate saved successfully.', 'success')
        return redirect(url_for('event.candidates_manage', event_id=event_id))
    return render_template('candidates/candidate_edit.html', candidate=candidate,event=event)


@bp.route('/<int:candidate_id>/delete', methods=['DELETE'])
def candidate_delete(candidate_id):
    '''Delete a candidate'''

    # Check if candidate exists
    candidate = get_candidate_by_id(candidate_id)
    if not candidate:
        abort(404, description=f"Candidate with id {candidate_id} not found!")

    # Check if event exists
    event = get_event_by_id(candidate['event_id'])
    if not event:
        abort(404, description=f"Event with id {candidate['event_id']} not found!")
    if not verify_theme_admin(event['theme_id']):
        abort(403, description=f"You are not allowed to delete this candidate.")

    # Check if the event is finished or approved
    now = datetime.now()
    if event['status'] in ('finished', 'approved'):
        return jsonify({'success': False, 'message': 'You cannot delete a candidate of an finished event.'})
    elif event['start_date'] < now and event['end_date'] > now:
        return jsonify({'success': False, 'message': 'You cannot delete a candidate of an ongoing event.'})

    delete_candidate(candidate_id)
    flash('Candidate deleted successfully.', 'success')

    return jsonify({'success': True})


@bp.route('/<int:candidate_id>')
def candidate_view(candidate_id):
    '''View a candidate'''
    candidate = get_candidate_by_id(candidate_id)
    event = get_event_by_id(candidate['event_id'])
    if not candidate:
        abort(404, description=f"Candidate with id {candidate_id} not found!")
    return render_template('candidates/candidate_view.html', candidate=candidate, event=event)
