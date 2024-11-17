from datetime import datetime
from flask import render_template, abort

from voting.blueprints.theme.models import get_theme_by_id
from voting.decorators import json_response, theme_role_required
from voting.response import Response


from .models import get_event_by_id
from ..canditate.models import get_candidates_by_event_id, get_candidates_with_votes_percentage
from voting.utility import get_current_datetime
from . import bp

@bp.route('/view/<int:event_id>')
def event_view(event_id):
    """Return the event view page"""
    event = get_event_by_id(event_id)
    if not event:
        abort(404, description=f"event with id {event_id} not found!")
    (status, status_message) = get_event_status_and_message(event)
    return render_template('events/event_view.html', event=event, status=status, status_message=status_message)


@bp.route('/result/<int:event_id>')
def event_result(event_id):
    """Return the result page of verified event"""
    event = get_event_by_id(event_id)
    if not event:
        abort(404, description=f"Event with id {event_id} not found!")
    if event['status'] != 'verified':
        abort(403, description="This event has not been verified!")

    candidates = get_candidates_with_votes_percentage(event_id)
    if not candidates or len(candidates) == 0:
        abort(400, description="No candidate found in this event!")

    winner = candidates[0]
    return render_template('events/event_result.html', event=event, candidates=candidates, winner=winner)

def mapping_event_status(event):
    """Determine the status of an event"""
    now = get_current_datetime()
    if event['status'] == 'published':
        if event['start_date'] > now:
            return 'in_plan'
        elif event['end_date'] < now:
            return 'verifying'
        else:
            return 'on_going'
    return event['status']

def get_event_status_and_message(event):
    """Determine the status and status tooltip by combining fields of stauts and date"""
    now = get_current_datetime()
    if event['status'] == 'verified':
        return event['status'], "The result of event has been verified!"
    elif event['status'] == 'published':
        if event['start_date'] > now:
            return 'in_plan', "This event is coming soon!"
        elif event['end_date'] < now:
            return 'verifying', "The result of event is being verified!"
        elif event['start_date'] < now < event['end_date']:
            return 'on_going', "This event is on going!"
    return event['status'], "unknown status"

@bp.route('/<int:event_id>/candidates')
@theme_role_required
def candidates_manage(event_id):
    '''Display all candidates of an event'''
    event = get_event_by_id(event_id)
    if not event:
        abort(404, description=f"Event with id {event_id} not found!")
    candidates = get_candidates_by_event_id(event_id)
    return render_template('candidates/candidates_mgmt.html', event=event, candidates=candidates,
                           can_edit=can_edit(event))

@bp.route('/<int:event_id>/theme')
@json_response
def get_theme_by_event_id(event_id):
    '''Get the theme of an event'''
    event = get_event_by_id(event_id)
    if not event:
        return Response.error(f"Event with id {event_id} not found!")
    theme = get_theme_by_id(event['theme_id'])
    return Response.success(data=theme)

def can_edit(event):
    """Determine if an event can be edited"""
    now = get_current_datetime()
    if event['status'] == 'approved':
        return False
    elif event['start_date'] < now:
        return False
    return True