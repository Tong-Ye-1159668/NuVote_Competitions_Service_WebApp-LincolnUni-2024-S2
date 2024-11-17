from datetime import timedelta

from flask import render_template, g, request, jsonify, abort

from voting.config import DEFAULT_DATE_FORMAT, MAX_TICKETS_PER_EVENT
from voting.decorators import theme_role_required
from voting.services import can_be_voted
from .models import get_event_by_id, get_most_popular_location
from ..canditate.models import get_candidates_and_votes, get_candidates_by_event_id
from ..user.models import get_user_by_id
from ..vote.models import abandon_vote_by_id, abandon_votes_by_ids, abandon_votes_by_ip, \
    get_daily_valid_votes_by_candidate_and_event, get_daily_votes_by_event, get_votes_by_event_and_user, \
    get_votes_by_filters, get_votes_group_by_ip_for_event, get_location_votes_by_event
from . import bp
from ...utility import get_current_datetime


@bp.route('/<int:event_id>/vote', methods=['GET'])
def event_vote(event_id):
    """The page view of vote for an event"""
    event = get_event_by_id(event_id)
    # if event not found, raise an error
    if not event:
        abort(404, description=f"event with id {event_id} not found")
    candidates = get_candidates_and_votes(event_id, g.user['user_id'] if g.user else 0)
    user = get_user_by_id(g.user['user_id'] if g.user else 0)
    popular_location = get_most_popular_location(event_id)
    location_votes = get_location_votes_by_event(event_id)
    print(location_votes)
    (can_vote, message) = can_be_voted(event)

    if g.user:
        my_votes = get_votes_by_event_and_user(event_id, g.user['user_id'])
        has_voted = my_votes and len(my_votes) >= MAX_TICKETS_PER_EVENT
        if can_vote:
            # if user has voted, can not vote again
            if has_voted:
                can_vote = False
                message = "You have voted!"
            # if user's role is not permitted to vote, can not vote
            # elif g.user['role'] not in PERMITTED_VOTE_ROLES:
            #     can_vote = False
            #     message = "Your role are not permitted to vote!"
    else:
        has_voted = False
    show_report_btn = False
    if event['status'] == 'published' and event['start_date'] < get_current_datetime():
        show_report_btn = True
    return render_template('events/event_vote.html', event=event, candidates=candidates, user=user,
                           popular_location=popular_location, can_vote=can_vote, has_voted=has_voted, message=message,
                           CURR_TIME=get_current_datetime(), show_report_btn=show_report_btn,  location_votes=location_votes)


@bp.route('/<int:event_id>/scrutineering')
@theme_role_required
def votes_scrutineering(event_id):
    '''Render the votes scrutineering page for an event'''
    event = get_event_by_id(event_id)
    if not event:
        abort(404, f'Event with id {event_id} not found')
    votes = get_votes_group_by_ip_for_event(event_id)
    return render_template('votes/votes_scrutineering.html', event=event, votes=votes)


@bp.route('/<int:event_id>/dailyvotes', methods=['GET'])
@theme_role_required
def daily_votes(event_id):
    '''Get daily votes for an event'''
    event = get_event_by_id(event_id)
    if not event:
        abort(404, f'Event with id {event_id} not found')

    daily_votes = get_daily_votes_by_event(event_id)

    # If there are votes, find the date range
    start_date = event['start_date'].date()
    end_date = event['end_date'].date()
    if daily_votes:
        start_date = min(daily_votes[0]['vote_date'], start_date)
        end_date = max(daily_votes[-1]['vote_date'], end_date)

    # Create a dictionary for all dates in the range with 0 votes initially
    date_range = {start_date + timedelta(days=x): {'valid_votes': 0, 'invalid_votes': 0}
                  for x in range((end_date - start_date).days + 1)}

    # Update the dictionary with actual vote data
    for vote in daily_votes:
        date_range[vote['vote_date']] = {
            'valid_votes': vote['valid_votes'],
            'invalid_votes': vote['invalid_votes']
        }

    # Convert the dictionary back to a sorted list
    complete_daily_votes = [
        {'vote_date': date.strftime(DEFAULT_DATE_FORMAT), 'valid_votes': values['valid_votes'],
         'invalid_votes': values['invalid_votes']}
        for date, values in sorted(date_range.items())
    ]

    response = {
        'success': True,
        'data': complete_daily_votes
    }
    return jsonify(response)


@bp.route('/<int:event_id>/votesbycandidates', methods=['GET'])
@theme_role_required
def daily_votes_of_candidates(event_id):
    '''Get daily votes for each candidate in an event'''
    # Fetch the event to ensure it exists
    event = get_event_by_id(event_id)
    if not event:
        abort(404, f'Event with id {event_id} not found')

    # Fetch the daily valid votes for each candidate
    daily_valid_votes = get_daily_valid_votes_by_candidate_and_event(event_id)

    start_date = event['start_date'].date()
    end_date = event['end_date'].date()
    if daily_valid_votes and len(daily_valid_votes) > 0:
        start_date = min(daily_valid_votes[0]['vote_date'], start_date)
        end_date = max(daily_valid_votes[-1]['vote_date'], end_date)

    # Create a date range from start_date to end_date
    date_range = [start_date + timedelta(days=i) for i in range((end_date - start_date).days + 1)]

    # Initialize a dictionary to hold cumulative vote counts for each candidate
    cumulative_votes = {}
    candidates = get_candidates_by_event_id(event_id)
    # Initialize cumulative votes with zero for each date and candidate
    for candidate in candidates:
        cumulative_votes[candidate['candidate_id']] = {
            'name': candidate['name'],
            'daily_votes': {date.strftime(DEFAULT_DATE_FORMAT): 0 for date in date_range}
        }

    # Accumulate votes for each candidate over the date range
    for vote in daily_valid_votes:
        vote_date_str = vote['vote_date'].strftime(DEFAULT_DATE_FORMAT)
        candidate_id = vote['candidate_id']
        cumulative_votes[candidate_id]['daily_votes'][vote_date_str] += vote['valid_votes']

    # Convert the dictionary into a list for JSON serialization
    response_data = []
    for candidate_id, data in cumulative_votes.items():
        cum_sum = 0
        votes_data = []
        for date in date_range:
            date_str = date.strftime(DEFAULT_DATE_FORMAT)
            cum_sum += data['daily_votes'][date_str]
            votes_data.append({'date': date_str, 'cumulative_votes': cum_sum})

        response_data.append({
            'candidate_id': candidate_id,
            'candidate_name': data['name'],
            'cumulative_votes': votes_data
        })

    # Return the response as JSON
    return jsonify(response_data)


@bp.route('/<int:event_id>/votes')
@theme_role_required
def votes_list(event_id):
    '''Render the votes list page for an event'''
    event = get_event_by_id(event_id)
    if not event:
        abort(404, f'Event with id {event_id} not found')

    candidates = get_candidates_by_event_id(event_id)
    ip = request.args.get('ip')
    return render_template('votes/votes_list.html', event=event, candidates=candidates, ip=ip)


@bp.route('/<int:event_id>/votes/query')
@theme_role_required
def votes_query(event_id):
    '''Query votes for an event'''
    event = get_event_by_id(event_id)
    if not event:
        return jsonify(success=False, message="Event not found"), 404

    ip = request.args.get('ip')
    status = request.args.get('status', 'true')
    candidate_id = request.args.get('candidate_id')

    votes = get_votes_by_filters(event_id, ip, status == 'true', candidate_id)
    return jsonify(success=True, votes=votes)


@bp.route('/<int:event_id>/votes/abandon/<int:vote_id>')
@theme_role_required
def abandon_vote(event_id, vote_id):
    '''Abandon a vote by id'''
    event = get_event_by_id(event_id)
    if not event or event['status'] in ['in_plan', 'approved']:
        return jsonify(success=False, message="Cannot abandon votes for this event."), 403

    updated_count = abandon_vote_by_id(vote_id)
    if updated_count:
        return jsonify(success=True, message="Vote disabled successfully.")
    return jsonify(success=False, message="Vote not found or already abandoned."), 404


@bp.route('/<int:event_id>/votes/abandonbyids', methods=['POST'])
@theme_role_required
def abandon_vote_batch(event_id):
    '''Abandon votes by ids'''
    vote_ids = request.form.getlist('vote_ids')
    vote_ids = request.get_json().get('vote_ids', [])

    # Check if vote_ids is empty
    if not vote_ids or len(vote_ids) == 0:
        return jsonify(success=False, message="Please select at least one vote to disable."), 400

    # Check if event is in plan or approved
    event = get_event_by_id(event_id)
    if not event or event['status'] in ['in_plan', 'approved']:
        return jsonify(success=False, message="Cannot abandon votes for this event."), 403

    # Disable votes
    updated_count = abandon_votes_by_ids(vote_ids)
    if updated_count:
        return jsonify(success=True, message=f'You have successfully disabled {updated_count} votes')
    return jsonify(success=False, message="Votes not found or already disabled."), 404


@bp.route('/<int:event_id>/votes/abandonbyip/<string:ip>')
@theme_role_required
def abandon_vote_batch_by_ip(event_id, ip):
    '''Abandon votes by ip'''

    # Check if event is in plan or approved
    event = get_event_by_id(event_id)
    if not event or event['status'] in ['verified']:
        return jsonify(success=False, message="Cannot disable votes for this event."), 403

    # Disable votes
    updated_count = abandon_votes_by_ip(ip)
    if updated_count:
        return jsonify(success=True, message=f'You have successfully disabled {updated_count} votes from IP {ip}')
    return jsonify(success=False, message="Votes not found or already disabled."), 404
