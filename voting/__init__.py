from voting.blueprints.theme.models import get_all_themes_managed_by_me
from voting.setup import init_upload_folders, integrate_form, install_template_filters
from flask import Flask, g, url_for
from voting.blueprints import register_blueprints
from voting.config import APP_NAME, DEFAULT_PROFILE_IMAGE, DEFAULT_SECRET_KEY, SLOGAN
from .login_manage import LoginManager
from .blueprints.user.models import get_user_by_id

app = Flask(__name__, static_folder='static', template_folder='templates')

app.secret_key = DEFAULT_SECRET_KEY

# set up Flask-WTF handling library
integrate_form(app)
# registering custom template filters for use in Jinja2 templates.
install_template_filters(app)

from . import index,error
# registering Flask blueprints with the main application
register_blueprints(app)


# initial of LoginManager
login_manager = LoginManager(app, login_view='user.login')

#  a decorator provided by Flask-Login
@login_manager.user_loader
# user loader function passing user_id as an argument
def load_user(user_id):
    # get user details by user id
    return get_user_by_id(user_id)

# a decorator that registers the function as a context processor. 
@app.context_processor
# run before the template is rendered and inject new values into the template context.
def inject_user():
    """inject global template variables"""
    # check if a user is logged in by verifying if 'user' exists in Flask's global g object and is not None.
    is_logged_in = 'user' in g and g.user is not None
    # set current_user to the user object if it exists in g, otherwise it's an empty dictionary.
    current_user = g.user if 'user' in g else {}
    #  check if there's a 'manage_role_for_theme' flag in g and its value.
    manage_role_for_theme = 'manage_role_for_theme' in g and g.manage_role_for_theme
    # This context processor makes app_name, slogan, current user etc. variables available in all templates without needing to pass them explicitly each time a template is rendered.
    return dict(APP_NAME=APP_NAME, SLOGAN=SLOGAN, CURRENT_USER=current_user, IS_LOGGED_IN=is_logged_in, MANAGE_ROLE_FOR_THEME=manage_role_for_theme, DEFAULT_PROFILE_IMAGE=DEFAULT_PROFILE_IMAGE, MY_THEME_MENUS=get_my_theme_menus())

def get_my_theme_menus():
    """get menus for my themes"""
    menus = []
    if 'manage_role_for_theme' in g and g.manage_role_for_theme:
        themes = get_all_themes_managed_by_me(g.user['user_id'])
        for theme in themes:
            menus.append({
                'name': theme['theme_name'],
                'url': url_for('theme.manage_theme', theme_id=theme['theme_id'])
            })
    return menus

# set up folders for file uploads in setup.py
init_upload_folders(app)
