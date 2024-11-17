import os
from flask_bootstrap import Bootstrap5
from flask_wtf import CSRFProtect
from voting.config import DEFAULT_EVENT_IMAGES_FOLDER, DEFAULT_CANDIDATE_IMAGES_FOLDER, DEFAULT_PROFILE_IMAGES_FOLDER, DEFAULT_CHARITY_IMAGES_FOLDER


def init_upload_folders(app):
    """Create all upload folders and save absolute path to app"""
    create_folder(app, 'PROFILE_IMAGES_ABS_PATH', DEFAULT_PROFILE_IMAGES_FOLDER)
    create_folder(app, 'CANDIDATE_IMAGES_ABS_PATH', DEFAULT_CANDIDATE_IMAGES_FOLDER)
    create_folder(app, 'EVENT_IMAGES_ABS_PATH', DEFAULT_EVENT_IMAGES_FOLDER)
    create_folder(app, 'CHARITY_IMAGES_ABS_PATH', DEFAULT_CHARITY_IMAGES_FOLDER)

# Create image upload directories and save to app


def create_folder(app, key, path):
    folder = os.path.join(app.root_path, path)
    if not os.path.exists(folder):
        os.makedirs(folder)
    app.config[key] = folder


def integrate_form(app):
    """Integrate web form and bootstrap"""
    app.config["WTF_CSRF_CHECK_DEFAULT"] = False
    # Bootstrap-Flask requires this line
    bootstrap = Bootstrap5(app)
    # Flask-WTF requires this line
    csrf = CSRFProtect(app)


def install_template_filters(app):
    from . import template_filters
