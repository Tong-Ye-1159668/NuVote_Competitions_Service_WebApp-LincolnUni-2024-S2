from functools import wraps

from flask import abort, current_app, g, jsonify, session, redirect, url_for

from voting.blueprints.event.models import get_event_by_id
from voting.blueprints.theme.models import get_theme_role_by_user

# check if the key 'user_id' is present in the Flask session object.
def is_logged_in():
    # return a boolean value True or False
    return 'user_id' in session


# this decorator is to protect routes that require authentication
def login_required(func):
    """the decorator for the method need user login

    Args:
        func (function): the decorated method
    """

    @wraps(func)
    def decorated_view(*args, **kwargs):
        # if no user is logged in 
        if g.user is None:
            # redirect to the login page at the endpoint for the login page.
            return redirect(url_for(current_app.login_manager.login_view))
        # if there is a logged in user, call the orgiinal function with all original arguments.
        return func(*args, **kwargs)

    return decorated_view


def roles_required(*roles):
    """the decorator for the method need verify user's role"""

    def wrapper(func):
        # ensure user is logged in
        @wraps(func)
        @login_required
        # return view if loggin use has relavant site wide role
        def decorated_view(*args, **kwargs):
            # if use has been assigned a role
            if g.user['role'] in roles:
                return func(*args, **kwargs)
            # if use has no proper role
            abort(
                403, description=f'Only role(s) {roles} can access this page.')

        return decorated_view

    return wrapper

# providing a higher level of access control than login_require by calling roles_required
def admin_required(func):
    return roles_required('siteAdmin')(func)# providing a higher level of access control than login_require by calling roles_required

def site_role_required(func):
        return roles_required('siteAdmin','siteHelper')(func)# providing a higher level of access control than login_require by calling roles_required


# either tAdmin and tScrutineer can access 
def theme_role_required(func):
    '''Decorator to check if the user is a theme role.'''
    # ensure user is logged in
    @wraps(func)
    @login_required
    # return view if loggin use has relavant theme role
    def decorated_function(*args, **kwargs):
        # if not tAdmin or tScrutineer, abort
        if not __verify_theme_role(kwargs, ('tAdmin', 'tScrutineer')):
            abort(403, description='Only theme admins or scrutineers can access this page.')
        return func(*args, **kwargs)
    return decorated_function

# only theme admin can access
def theme_admin_required(func):
    '''Decorator to check if the user is a theme admin.'''
     # ensure user is logged in
    @wraps(func)
    @login_required
    # return view if loggin use is a theme admin
    def decorated_function(*args, **kwargs):
        # if not tAdmin, abort
        if not __verify_theme_role(kwargs, ['tAdmin']):
            abort(403, description='Only theme admins can access this page.')
        return func(*args, **kwargs)
    return decorated_function

# only owner can access
def owner_required(f):
    '''Decorator to check if the user is the owner of the resource.'''

    @wraps(f)
    def decorated_function(*args, **kwargs):
        user_id = kwargs.get('user_id')
        # if current user id (stored in session) not equal to the profile's user ID
        if user_id != session['user_id']:
            # not allow to access
            abort(500, description=f'Only owner can do this operation.')
        return f(*args, **kwargs)

    return decorated_function

def json_response(func):
    @wraps(func)
    # wrapper will replace the original function. It accepts any positionla and keyword arguments.
    def decorated_function(*args, **kwargs):
        result = func(*args, **kwargs)
        #  If the original function returns None, an empty JSON object is returned
        if result is None:
            return jsonify({})
        # If the result is not None, it converts the result's attributes to a dictionary and then to a JSON response
        return jsonify(result.__dict__)
    # return the wrapper function
    return decorated_function

# role-based access for themes and events. kwargs: dictionary; roles: a list of roles
def __verify_theme_role(kwargs, roles):
    # get theme_id from kwargs
    theme_id = kwargs.get('theme_id')
    if not theme_id:
        # get event_id from kwargs
        event_id = kwargs.get('event_id')
        # find event details
        event = get_event_by_id(event_id)
        # If an event is found, it uses the theme_id from the event. If no event is found, it returns False
        if not event:
            return False
        theme_id = event['theme_id']

    # get user's role for associated theme_id   
    theme_role = get_theme_role_by_user(theme_id, g.user['user_id'])
    # return role if exist
    return theme_role is not None and theme_role['role'] in roles
