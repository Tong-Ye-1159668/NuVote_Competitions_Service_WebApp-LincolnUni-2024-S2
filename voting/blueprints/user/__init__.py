from flask import Blueprint


bp = Blueprint('user', __name__, url_prefix="/users", template_folder="templates")

from . import login_out, profile, register, users_mgmt