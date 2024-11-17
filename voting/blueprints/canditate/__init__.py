
from flask import Blueprint


bp = Blueprint('candidate', __name__, url_prefix="/candidates", template_folder="templates")

from . import routes