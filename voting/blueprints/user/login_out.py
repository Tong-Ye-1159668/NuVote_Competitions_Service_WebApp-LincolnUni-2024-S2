from flask import request, session, redirect, url_for, flash, render_template, current_app as app
from flask_wtf.csrf import generate_csrf

from voting.blueprints.theme.models import get_theme_roles
from voting.decorators import is_logged_in

from .models import is_user_password_valid_by_username, get_user_by_username
from voting.utility import are_fields_present
from . import bp

LOGIN_REQUIRED_FIELDS = ['username', 'password']


# Route for login page (supports both GET and POST requests)
@bp.route('/login', methods=['GET', 'POST'])
def login():
    '''Login page'''

    # Redirect to index if user is already logged in
    if is_logged_in():
        return redirect(url_for('index'))
    
    # Handle POST request
    if request.method == 'POST':
        username = ''
        if are_fields_present(request, LOGIN_REQUIRED_FIELDS):
            username = request.form['username']
            user_password = request.form['password']

            # Check if the user exist
            user = get_user_by_username(username)
            if user:
                # Check if the password is correct
                if is_user_password_valid_by_username(username, user_password):
                    if user['status'] == 'inactive':
                        flash('User account is inactive!', 'danger')
                        return render_template('user/login.html', username=username)
                    theme_roles = get_theme_roles(user['user_id'])
                    app.login_manager.login_user(user, theme_roles and len(theme_roles) > 0)
                    # login_user(user)
                    return redirect(url_for('index'))

            flash('Invalid username or password!', 'danger')
            return render_template('user/login.html', username=username)
        flash('Username and password are required!', 'danger')
        return render_template('user/login.html', username=username)

    session['_csrf_token'] = generate_csrf()
    # Show the login form with message (if any)
    return render_template('user/login.html', csrf_token=session.get('_csrf_token'))


# Route for logout page
@bp.route('/logout')
def logout():
    '''Logout the user'''
    app.login_manager.logout_user()
    return redirect(url_for('index'))
