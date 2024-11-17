from flask import abort, g, render_template, request, url_for

from voting.blueprints.event.events import mapping_event_status
from voting.utility import get_current_datetime
from ..donation.models import get_approved_charities_by_theme

from ..event.models import get_events_by_theme_id, get_nondraft_events_by_theme_id
from voting.decorators import admin_required, json_response, login_required, theme_admin_required, theme_role_required
from voting.response import Response
from voting.signals import theme_submitted, theme_approved, theme_role_granted, theme_role_changed, theme_role_revoked
from .forms import ThemeForm
from . import models
from . import bp


@bp.route('/')
def index():
    themes = models.get_all_accepted_themes_with_images()
    return render_template('theme/index.html', themes=themes, form=ThemeForm())


@bp.route("/view/<int:theme_id>")
def view(theme_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        abort(404, 'Theme not found')
    events = get_nondraft_events_by_theme_id(theme_id)
    charities = get_approved_charities_by_theme(theme_id)
    return render_template('theme/theme_view.html', theme=theme, events=events, charities=charities, CURR_TIME=get_current_datetime())


@bp.route("/manage/<int:theme_id>")
def manage_theme(theme_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        abort(404, 'Theme not found')
    events = get_events_by_theme_id(theme_id)
    for event in events:
        event['mapping_status'] = mapping_event_status(event)
    theme_role = models.get_theme_role_by_user(theme_id, g.user['user_id'])
    return render_template('theme/theme_manage.html', theme=theme, events=events, CURR_TIME=get_current_datetime(),
                           CURRENT_THEME_ROLE=theme_role['role'] if theme_role else None)


@bp.route('/new', methods=['POST'])
@login_required
@json_response
def create_theme():
    theme_name = request.form['theme_name']
    theme_description = request.form['theme_description']
    enable_location = request.form.get('enable_location', False)
    if not theme_name or not theme_description:
        return Response.error('Theme name and description are required!')
    created_by = g.user['user_id']
    theme_id = models.create_theme(theme_name, theme_description, created_by, enable_location)
    theme_submitted_notification(theme_id)
    return Response.success('Theme created successfully!', theme_id)


@bp.route('/delete/<int:theme_id>', methods=['DELETE'])
@login_required
@json_response
def delete_theme(theme_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        return Response.error('Theme not found!')
    if theme['created_by'] != g.user['user_id']:
        return Response.error('Only creator can delete theme!')
    if theme['approved'] == 1:
        return Response.error('Theme already approved!')
    deleted_rows = models.delete_theme(theme_id)
    if deleted_rows == 0:
        return Response.error('Theme not found!')
    return Response.success('Theme deleted successfully!')


@bp.route('/my')
@login_required
def my_proposals():
    themes = models.get_all_themes_created_by_user(g.user['user_id'])
    return render_template('theme/my_proposals.html', themes=themes, form=ThemeForm())


@bp.route('/approve/<int:theme_id>', methods=['POST'])
@admin_required
@json_response
def approve_theme(theme_id):
    '''Approve a theme'''
    return handle_approve_theme(theme_id, True)


@bp.route('/reject/<int:theme_id>', methods=['POST'])
@admin_required
@json_response
def reject_theme(theme_id):
    '''Reject a theme'''
    return handle_approve_theme(theme_id, False)


# @bp.route('/manage')
# def manage_themes():
#     manage = models.get_all_themes_managed_by_me(g.user['user_id'])
#     pending = models.get_all_pending_themes()
#     approved = models.get_all_approved_themes()
#     return render_template('theme/themes_mgmt.html', manage=manage, pending=pending, approved=approved)

@bp.route('/approve')
@admin_required
def approve_themes():
    '''Theme approval page'''
    pending = models.get_all_pending_themes()
    approved = models.get_all_approved_themes()
    return render_template('theme/themes_mgmt.html', pending=pending, approved=approved)

# ================================================================
# Theme Roles
# ================================================================

@bp.route('/<int:theme_id>/roles')
@theme_role_required
def manage_theme_roles(theme_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        abort(404, 'Theme not found')
    theme_roles = models.get_theme_roles_by_theme_id(theme_id)
    users = models.get_users_without_theme_role(theme_id)
    curr_theme_role = models.get_theme_role_by_user(theme_id, g.user['user_id'])
    return render_template('theme/theme_roles.html', theme=theme, theme_roles=theme_roles, users=users,
                           CURRENT_THEME_ROLE=curr_theme_role['role'] if curr_theme_role else None)


@bp.route('/<int:theme_id>/role/add', methods=['POST'])
@theme_admin_required
@json_response
def add_theme_role(theme_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        return Response.error('Theme not found!')
    user_id = request.form['user_id']
    role = request.form['role']
    theme_role_id = models.add_role_to_theme(theme_id, user_id, role, g.user['user_id'])
    theme_role_granted_notification(theme_role_id)
    return Response.success('Theme role added successfully!')


@bp.route('/<int:theme_id>/role/update/<int:theme_role_id>', methods=['POST'])
@theme_admin_required
@json_response
def update_role(theme_id, theme_role_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        return Response.error('Theme not found!')
    role = request.json['role']
    theme_role = models.get_theme_role_by_id(theme_role_id)
    if not theme_role:
        return Response.error('Theme role not found!')
    if theme_role['user_id'] == g.user['user_id']:
        return Response.error('You cannot change your own role!')
    models.update_theme_role(theme_role_id, role)
    theme_role_changed_notification(theme_role_id)
    return Response.success('Role updated successfully!')


@bp.route('/<int:theme_id>/role/remove/<int:theme_role_id>', methods=['POST'])
@theme_admin_required
@json_response
def remove_theme_role(theme_id, theme_role_id):
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        return Response.error('Theme not found!')
    theme_role = models.get_theme_role_by_id(theme_role_id)
    if not theme_role:
        return Response.error('Theme role not found!')
    if theme_role['user_id'] == g.user['user_id']:
        return Response.error('You cannot remove yourself from the theme!')
    models.remove_role_from_theme(theme_role_id)
    theme_role_revoked_notification(theme_role_id)
    return Response.success('Theme role removed successfully!')


def handle_approve_theme(theme_id, accepted):
    '''Approve or reject a theme'''
    theme = models.get_theme_by_id(theme_id)
    if not theme:
        return Response.error('Theme not found!')
    if theme['approved'] == 1:
        return Response.error('Theme already approved!')
    if theme['created_by'] == g.user['user_id']:
        return Response.error(f'You cannot {"approve" if accepted else "reject"} your own theme!')
    models.approve_theme(theme_id, g.user['user_id'], accepted)
    if accepted:
        models.add_role_to_theme(theme_id, theme['created_by'], 'tAdmin', g.user['user_id'])
    theme_approved_notification(theme, accepted)
    return Response.success(f'Theme {"approved" if accepted else "rejected"} successfully!')


def theme_submitted_notification(theme_id):
    theme = models.get_theme_by_id(theme_id)
    content = f'Theme "{theme["theme_name"]}" has been submitted, please approve it in time.'
    url = url_for('theme.approve_themes', _anchor=f'theme-{theme["theme_id"]}')
    theme_submitted.send(theme, content=content, url=url)


def theme_approved_notification(theme, accepted):
    content = f'Theme "{theme["theme_name"]}" has been {"approved" if accepted else "rejected"}.'
    url = url_for('theme.my_proposals', _anchor=f'theme-{theme["theme_id"]}')
    theme_approved.send(theme, content=content, url=url)

def theme_role_granted_notification(theme_role_id):
    theme_role = models.get_theme_role_by_id(theme_role_id)
    theme = models.get_theme_by_id(theme_role['theme_id'])
    content = f'You have been granted the role of {theme_role["role"]} in theme "{theme["theme_name"]}".'
    url = url_for('theme.manage_theme_roles', theme_id=theme_role['theme_id'])
    theme_role_granted.send(theme_role, content=content, url=url)

def theme_role_changed_notification(theme_role_id):
    theme_role = models.get_theme_role_by_id(theme_role_id)
    theme = models.get_theme_by_id(theme_role['theme_id'])
    content = f'Your role in theme "{theme["theme_name"]}" has been changed to {theme_role["role"]}.'
    url = url_for('theme.manage_theme_roles', theme_id=theme_role['theme_id'])
    theme_role_changed.send(theme_role, content=content, url=url)

def theme_role_revoked_notification(theme_role_id):
    theme_role = models.get_theme_role_by_id(theme_role_id)
    theme = models.get_theme_by_id(theme_role['theme_id'])
    content = f'Your role in theme "{theme["theme_name"]}" has been revoked.'
    url = url_for('theme.manage_theme_roles', theme_id=theme_role['theme_id'])
    theme_role_revoked.send(theme_role, content=content, url=url)

