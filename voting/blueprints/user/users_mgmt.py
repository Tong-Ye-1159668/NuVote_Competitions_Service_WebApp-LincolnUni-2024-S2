# MANAGING USERS
import re

from flask import abort, g, render_template, request, flash, redirect, url_for

from voting import config
from voting.blueprints.theme.models import get_all_accepted_themes, get_theme_by_id
from voting.blueprints.vote.models import get_recent_votes_by_user
from voting.decorators import admin_required, json_response, login_required
from voting.response import Response
from voting.services import  verify_site_admin, verify_site_admin_or_theme_roles
from . import models
from voting.utility import are_fields_present
from . import bp
from voting.signals import user_banned, user_ban_appealed, user_ban_appeal_approved, user_ban_appeal_rejected, user_ban_revoked, user_role_changed

CREATE_USER_REQUIRED_FIELDS = ['username', 'email', 'password', 'password2', 'first_name', 'last_name', 'location']


@bp.route('/')
@admin_required
def users_mgmt():
    '''Render the admin home page with user information'''
    return render_template('user/users_mgmt.html')


@bp.route('/create', methods=['GET', 'POST'])
@admin_required
def user_create():
    '''Create a new scrutineer account or admin account'''
    if request.method == 'POST':
        form_data = request.form.to_dict()

        if are_fields_present(request, CREATE_USER_REQUIRED_FIELDS):

            username = request.form['username']
            email = request.form['email']
            password = request.form['password']

            # Validation checks
            if not re.match(config.EMAIL_REGEX, email):
                flash('Invalid email address!', 'danger')
                form_data.pop('email', None)
                return render_user_create(form_data)

            if not re.match(config.USERNAME_REGX, username):
                flash('Username must contain only characters and numbers!', 'danger')
                form_data.pop('username', None)
                return render_user_create(form_data)

            if not re.match(config.DEFAULT_PASSWORD_REGEX, password):
                flash('Password must be at least 8 characters long and include a mix of letters, numbers, '
                      'and special characters (@$!%*?&)!', 'danger')
                form_data.pop('password', None)
                form_data.pop('password2', None)
                return render_user_create(form_data)

            if password != request.form['password2']:
                flash('Password does not match', 'danger')
                form_data.pop('password', None)
                form_data.pop('password2', None)
                return render_user_create(form_data)

            if models.get_user_by_username(username):
                flash('Account already exists!', 'danger')
                form_data.pop('username', None)
                return render_user_create(form_data)

            if models.get_user_by_email(email):
                flash('Email already exists!', 'danger')
                form_data.pop('email', None)
                return render_user_create(form_data)

            # all form data are valid
            user = models.User(username, request.form['first_name'], request.form['last_name'],
                        request.form['location'], email, config.DEFAULT_USER_DESCRIPTION, password,
                        request.form['role'])
            models.create_user(user)
            flash(f'{username} have successfully created!', 'success')
            return redirect(url_for('user.users_mgmt'))
        else:
            # Form is empty (no POST data)
            flash('Please fill out the form!', 'danger')
            return render_user_create(form_data)
    return render_user_create(None)


@bp.route('/search', methods=['GET'])
@admin_required
@json_response
def search_users():
    '''Search for users by query, role, and status'''
    query = request.args.get('query', '')
    role = request.args.get('role', '')
    status = request.args.get('status', '')
    users = models.search_users(query, role, status)
    return Response.success('Users fetched successfully', users)

@bp.route('/role/<int:user_id>', methods=['POST'])
@admin_required
@json_response
def update_user_role(user_id):
    '''Update the role of a user'''
    user = models.get_user_by_id(user_id)
    if not user:
        return Response.error('User not found!')
    new_role = request.json['role']
    if not new_role:
        return Response.error('Role is required!')
    if not new_role in config.USER_ROLES:
        return Response.error('Role is invalid!')
    models.update_user_role(user_id, new_role)
    user_role_changed_notofication(user)
    return Response.success('Role of user have successfully updated!')


@bp.route('/status/<int:user_id>', methods=['POST'])
@admin_required
@json_response
def update_user_status(user_id):
    '''Update the status of a user'''
    user = models.get_user_by_id(user_id)
    if not user:
        return Response.error('User not found!')
    new_status = request.json['status']
    if not new_status:
        return Response.error('Status is required!')
    models.update_user_status(user_id, new_status)
    return Response.success('Status of user have successfully updated!')

@bp.route('<int:user_id>/votes', methods=['GET'])
@login_required
@json_response
def recent_votes(user_id):
    """Retrieve recent votes for the logged-in user"""
    if not (verify_site_admin() or user_id == g.user['user_id']):
        return Response.error('You are not allowed to view this page!')
    recent_votes = get_recent_votes_by_user(user_id)
    return Response.success('Recent votes fetched successfully', recent_votes)

#==============================================================
# Banned Users
#==============================================================


@bp.route('/banned/search', methods=['GET'])
@login_required
@json_response
def search_banned_users():
    '''Search for banned users by theme_id, user_id, and revoked status'''
    theme_id = request.args.get('theme_id', '')
    user_id = request.args.get('user_id', '')
    revoked = request.args.get('revoked', '')
    banned_users = models.search_banned_users(theme_id, user_id, revoked)
    for banned_user in banned_users:
        appeals = models.get_appeals_by_ban_id(banned_user['ban_id'])
        banned_user['appeals'] = appeals
    return Response.success('Banned users fetched successfully', banned_users)


@bp.route('/banned')
def manage_banned_users():
    '''Manage banned users for the theme or site wide'''
    theme_id = int(request.args.get('theme_id', 0))
    if not verify_site_admin_or_theme_roles(theme_id):
        abort(403, 'You are not allowed to view this page')

    theme = None
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        if not theme:
            abort(404, 'Theme not found')
    users = models.get_users()
    themes = get_all_accepted_themes()
    return render_template('user/banned_users.html', theme=theme, users=users, themes=themes)

@bp.route('/ban', methods=['POST'])
@json_response
def ban_user():
    '''Ban a user for a theme or site wide'''
    theme_id = int(request.form['theme_id'])
    if not theme_id:
        theme_id = 0

    if not verify_site_admin_or_theme_roles(theme_id):
        return Response.error('You are not allowed to ban users!')

    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        if not theme:
            return Response.error('Theme not found!')
        
    user_id = request.form['user_id']
    banned_by = g.user['user_id']
    banned_reason = request.form['banned_reason']
    if not user_id or not banned_reason:
        return Response.error('User ID and banned reason are required!')
    exists = models.get_ban_by_theme_and_user(theme_id, user_id)
    if exists:
        return Response.error('User already banned!')
    ban_id = models.ban_user(theme_id, user_id, banned_by, banned_reason)
    user_banned_notification(ban_id)
    return Response.success('User banned successfully!')

@bp.route('/ban/revoke/<int:ban_id>', methods=['POST'])
@json_response
def revoke_ban(ban_id):
    '''Revoke a ban for a theme or site wide'''
    ban = models.get_ban_by_id(ban_id)
    if not ban:
        return Response.error('Ban not found!')
    if not verify_site_admin_or_theme_roles(ban['theme_id']):
        return Response.error('You are not allowed to revoke this ban!')
    
    if ban['theme_id'] != 0:
        theme = get_theme_by_id(ban['theme_id'])
        if not theme:
            return Response.error('Theme not found!')
    if ban['revoked'] == 1:
        return Response.error('Ban already revoked!')
    revoked_by = g.user['user_id']
    models.revoke_ban(ban_id, revoked_by)
    user_ban_revoked_notification(ban_id)
    return Response.success('Ban revoked successfully!')

@bp.route('<int:user_id>/ban/query', methods=['GET'])
@json_response
def is_user_banned(user_id):
    '''Check if a user is banned for a theme or site wide'''
    theme_id = int(request.args.get('theme_id', 0))
    if not verify_site_admin_or_theme_roles(theme_id):
        return Response.error('You are not allowed to view this page!')
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        if not theme:
            return Response.error('Theme not found!')
    ban = models.get_ban_by_theme_and_user(theme_id, user_id)
    return Response.success('success', ban is not None)

@bp.route('/ban/<int:ban_id>/appeals')
@json_response
def get_appeals_by_ban_id(ban_id):
    '''Get appeals by ban_id'''
    ban = models.get_ban_by_id(ban_id)
    if not ban:
        return Response.error('Banned user not found!')
    if not verify_site_admin_or_theme_roles(ban['theme_id']):
        return Response.error('You are not allowed to view this appeal!')
    appeals = models.get_appeals_by_ban_id(ban_id)
    return Response.success('success', appeals)

@bp.route('/appeal/<int:appeal_id>/approve', methods=['POST'])
@json_response
def approve_appeal(appeal_id):
    '''Approve an appeal for a theme or site wide'''
    appeal = models.get_appeal_by_id(appeal_id)
    if not appeal:
        return Response.error('Appeal not found!')
    ban = models.get_ban_by_id(appeal['ban_id'])
    if not ban:
        return Response.error('Ban not found!')
    if not verify_site_admin_or_theme_roles(ban['theme_id']):
        return Response.error('You are not allowed to approve this appeal!')
    if ban['revoked'] == 1:
        return Response.error('Ban already revoked!')
    models.approve_appeal(appeal_id, g.user['user_id'])
    models.revoke_ban(appeal['ban_id'], g.user['user_id'])
    user_appeal_approved_notification(appeal_id)
    return Response.success('Appeal approved successfully!')

@bp.route('/appeal/<int:appeal_id>/reject', methods=['POST'])
@json_response
def reject_appeal(appeal_id):
    '''Reject an appeal for a theme or site wide'''
    appeal = models.get_appeal_by_id(appeal_id)
    if not appeal:
        return Response.error('Appeal not found!')
    ban = models.get_ban_by_id(appeal['ban_id'])
    if not ban:
        return Response.error('Ban not found!')
    if not verify_site_admin_or_theme_roles(ban['theme_id']):
        return Response.error('You are not allowed to reject this appeal!')
    if ban['revoked'] == 1:
        return Response.error('Ban already revoked!')
    models.reject_appeal(appeal_id, g.user['user_id'])
    return Response.success('Appeal rejected successfully!')

@bp.route('/ban/<int:ban_id>/appeal', methods=['POST'])
@login_required
@json_response
def submit_appeal(ban_id):
    '''Submit an appeal for a theme or site wide'''
    appealed_reason = request.form['appealed_reason']
    if not appealed_reason:
        return Response.error('Appeal reason is required!')
    ban = models.get_ban_by_id(ban_id)
    if not ban:
        return Response.error('Ban not found!')
    if ban['revoked'] == 1:
        return Response.error('Ban already revoked!')
    appeals = models.get_appeals_by_ban_id(ban_id)
    if any(appeal['processed'] == 0 for appeal in appeals):
        return Response.error('You have already submitted an appeal pending for approval!')
    if ban['user_id'] != g.user['user_id']:
        return Response.error("You can only submit appeal for yourself!")
    appeal_id = models.create_appeal(ban_id, appealed_reason)
    user_appeal_notification(appeal_id)
    return Response.success('Appeal submitted successfully!')

@bp.route('/<int:user_id>/bans')
@login_required
@json_response
def get_bans_of_user(user_id):
    '''Get my bans'''
    bans = models.get_bans_by_user_id(user_id)
    for ban in bans:
        ban['appeals'] = models.get_appeals_by_ban_id(ban['ban_id'])
    return Response.success('success', bans)

def render_user_create(form_data):
    '''Render the user create page with the submitted form data'''
    submitted_form = {}
    if form_data is not None:
        submitted_form = {field: form_data[field] if field in form_data and form_data[field] else '' for field in
                          CREATE_USER_REQUIRED_FIELDS}
    return render_template('user/user_create.html', form_data=submitted_form)



def user_banned_notification(ban_id):
    ban = models.get_ban_by_id(ban_id)
    theme_id = ban['theme_id']
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        content = f'You have been banned from {theme["theme_name"]} for {ban["banned_reason"]}.'
        url = url_for('user.my_dashboard', view='bans')
    else:
        content = f'You have been banned from the site for {ban["banned_reason"]}.'
        url = url_for('user.my_dashboard', view='bans')
    user_banned.send(ban, content=content, url=url)

def user_appeal_notification(appeal_id):
    appeal = models.get_appeal_by_id(appeal_id)
    user = models.get_user_by_id(appeal['user_id'])
    ban = models.get_ban_by_id(appeal['ban_id'])
    theme_id = ban['theme_id']
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        content = f'User {user["username"]} has appealed to revoke ban from theme {theme["theme_name"]}.'
        url = url_for('user.manage_banned_users', theme_id=theme_id)
    else:
        content = f'User {user["username"]} has appealed to revoke ban from the site wide.'
        url = url_for('user.manage_banned_users')
    user_ban_appealed.send(appeal, content=content, url=url)

def user_appeal_approved_notification(appeal_id):
    appeal = models.get_appeal_by_id(appeal_id)
    ban = models.get_ban_by_id(appeal['ban_id'])
    theme_id = ban['theme_id']
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        content = f'Your appeal to revoke ban from theme {theme["theme_name"]} has been approved.'
    else:
        content = f'Your appeal to revoke ban from the site has been approved.'
    url = url_for('user.my_dashboard', view='bans')
    user_ban_appeal_approved.send(appeal, content=content, url=url)

def user_appeal_rejected_notification(appeal_id):
    appeal = models.get_appeal_by_id(appeal_id)
    ban = models.get_ban_by_id(appeal['ban_id'])
    theme_id = ban['theme_id']
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        content = f'Your appeal to revoke ban from theme {theme["theme_name"]} has been rejected.'
    else:
        content = f'Your appeal to revoke ban from the site has been rejected.'
    url = url_for('user.my_dashboard', view='bans')
    user_ban_appeal_rejected.send(appeal, content=content, url=url)

def user_ban_revoked_notification(ban_id):
    ban = models.get_ban_by_id(ban_id)
    theme_id = ban['theme_id']
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        content = f'Your ban from theme {theme["theme_name"]} has been revoked.'
    else:
        content = f'Your ban from the site has been revoked.'
    url = url_for('user.my_dashboard', view='bans')
    user_ban_revoked.send(ban, content=content, url=url)

def user_role_changed_notofication(user):
    content = f'Your role has been change to {user["role"]}.'
    url = url_for('user.my_dashboard', view='details')
    user_role_changed.send(user, content=content, url=url)