from flask import Blueprint


bp = Blueprint('announcement', __name__, url_prefix="/announcements", template_folder="templates")

from . import routes