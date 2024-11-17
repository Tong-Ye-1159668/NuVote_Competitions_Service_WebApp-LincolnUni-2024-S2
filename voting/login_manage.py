from functools import wraps

from flask import session, g, redirect, url_for, current_app, abort


class LoginManager:
    """Handling login and role verifying"""
    
    # Initializes the LoginManager with an app and a login view
    def __init__(self, app, login_view='login'):
        self.app = app
        # If an app is provided, it calls init_app
        if app:
            self.init_app(app, login_view)
        self.user_loader_callback = None

    # Sets up the LoginManager for a specific Flask app
    def init_app(self, app, login_view):
        """ init method, if login_view is not the default value "login", please init at first """
        self.app = app
        self.login_view = login_view
        app.before_request(self._load_user_from_session)
        app.login_manager = self

    # Allows setting a callback function to load a user given a user_id
    def user_loader(self, callback):
        """ Decorator for method loading user by user_id"""
        self.user_loader_callback = callback
        return callback

    def login_user(self, user, manage_role_for_theme=False):
        """please invoke this method after user login

        Args:
            user (dict): user dict, must has a key "user_id"
        """
        # Stores user_id and manage_role_for_theme in the session
        session['user_id'] = user["user_id"]
        session['manage_role_for_theme'] = manage_role_for_theme
        # Sets user and manage_role_for_theme in Flask's g object
        g.user = user
        g.manage_role_for_theme = manage_role_for_theme
        
    def logout_user(self):
        """invoke this method after logout"""
        # clearup session for user_id and role
        session.pop('user_id', None)
        session.pop('manage_role_for_theme', None)
        # clearup g user
        g.user = None

    def _load_user_from_session(self):
        """Invoke before request, set global user from user_id in session"""
        # get user_id from session
        user_id = session.get('user_id')
        # Uses the user_loader callback to get the user object
        if user_id and self.user_loader_callback:
            g.user = self.user_loader_callback(user_id)
            g.manage_role_for_theme = session.get('manage_role_for_theme')
        # if user_id is none, clearup g
        else:
            g.user = None
            g.manage_role_for_theme = False



