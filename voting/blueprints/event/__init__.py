from flask import Blueprint


bp = Blueprint('event', __name__, url_prefix="/events", template_folder="templates")

from . import event_vote, events, events_mgmt