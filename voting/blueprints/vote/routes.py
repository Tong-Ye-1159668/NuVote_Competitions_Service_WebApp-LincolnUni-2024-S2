from flask import g

from voting.response import Response

from ..theme.models import get_theme_by_id
from ..user.models import get_ban_by_theme_and_user

from .models import create_vote, get_votes_by_event_and_user
from voting.config import DEFAULT_VOTE_VALID, MAX_TICKETS_PER_EVENT
from voting.decorators import json_response, roles_required
from voting.services import can_be_voted
from voting.utility import get_real_ip
from ..event.models import get_event_by_id
from ..canditate.models import get_candidate_by_id
from flask import request
from . import bp


@bp.route('/new', methods=['POST'])
@json_response
def vote():
    """Cast a new vote"""
    candidate_id = request.json['candidate_id']
    # if candidate_id is not present, raise an error
    if not candidate_id:
        return Response.error('Candidate_id is required!')

    # if candidate not found, raise an error
    candidate = get_candidate_by_id(candidate_id)
    if not candidate:
        return Response.error('Candidate not found!')

    # if event not found, raise an error
    event = get_event_by_id(candidate['event_id'])
    if not event:
        return Response.error('Event not found!')

    can_vote, message = can_be_voted(event)
    if not can_vote:
        return Response.error(message)

    theme = get_theme_by_id(event['theme_id'])
    if not theme:
        return Response.error('Theme not found!')

    ban = get_ban_by_theme_and_user(theme['theme_id'], g.user['user_id'])
    if ban is not None:
        return Response.error('You are banned for voting in this theme!')
    ban = get_ban_by_theme_and_user(0, g.user['user_id'])
    if ban is not None:
        return Response.error('You are banned for voting site wide!')

    # if user has voted, can not vote again
    my_votes = get_votes_by_event_and_user(event['event_id'], g.user['user_id'])
    if my_votes and len(my_votes) >= MAX_TICKETS_PER_EVENT:
        return Response.error('You have voted, can not vote again!')

    vote = {
        'candidate_id': candidate_id,
        'voted_by': g.user['user_id'],
        'event_id': event['event_id'],
        'voted_ip': get_real_ip(request),
        'valid': DEFAULT_VOTE_VALID
    }

    vote_id = create_vote(vote)

    return Response.success('Vote created successfully!', {'vote_id': vote_id})
