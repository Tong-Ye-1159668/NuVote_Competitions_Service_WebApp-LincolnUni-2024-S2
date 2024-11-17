from flask import Blueprint


bp = Blueprint('ticket', __name__, url_prefix="/tickets", template_folder="templates")

from . import routes