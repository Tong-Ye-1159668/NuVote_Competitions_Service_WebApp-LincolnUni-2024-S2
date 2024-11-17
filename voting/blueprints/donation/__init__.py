from flask import Blueprint

bp = Blueprint('donation', __name__, url_prefix="/donations", template_folder="templates")

from . import routes
