from flask import Blueprint

bp = Blueprint('theme', __name__, url_prefix="/themes", template_folder="templates")

from . import routes