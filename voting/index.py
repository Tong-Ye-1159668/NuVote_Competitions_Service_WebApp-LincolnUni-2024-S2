from flask import render_template

from .blueprints.announcement.models import get_latest_active_announcement
from .blueprints.donation.models import get_all_approved_charities
from .blueprints.event.models import get_latest_events
from .blueprints.user.models import get_latest_voted_users
from voting.utility import get_current_datetime
from . import app

# Route for home page (supports both GET request)
@app.route('/')
def index():
    '''Show the index/home page'''
    # retrieve the latest announcement
    latest_announcement = get_latest_active_announcement()
    # get latest events
    events = get_latest_events()
    # filter out draft events
    refined = [c for c in events if c['status'] != 'draft']
    # get the current datetime
    now = get_current_datetime()
    # get latest voter
    latest_voted_users = get_latest_voted_users()
    latest_voted_users.reverse()
    # get all approved charities
    charities = get_all_approved_charities()
    refined_charity = [ch for ch in charities if ch['approved'] != 0]
    # pass to home page index.html
    return render_template('index.html',
                           announcement=latest_announcement, events=refined, charities=refined_charity, CURR_TIME=now,
                           users=latest_voted_users)
