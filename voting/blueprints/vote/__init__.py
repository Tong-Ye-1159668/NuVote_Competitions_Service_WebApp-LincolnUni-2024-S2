
from flask import Blueprint


bp = Blueprint('vote', __name__, url_prefix="/votes", template_folder="templates")

from . import routes
