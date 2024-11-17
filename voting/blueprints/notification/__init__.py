from flask import Blueprint

bp = Blueprint('notification', __name__, url_prefix="/notifications", template_folder="templates")

from . import routes

from . import signal_receivers