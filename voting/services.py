
from flask import g
from voting.blueprints.theme.models import get_theme_role_by_user
from .utility import get_current_datetime

def can_be_voted(event):
    """If an event can be voted and alert message depends on status and start and end date"""
    now = get_current_datetime()
    message = "This event is not available yet."
    if not event or event['status'] in ('draft'):
        return False, message
    if event['status'] == 'verified':
        return False, "This event is over, and the results have been published!"
    if event['start_date'] > now:
        return False, "This event hasn't started yet!"
    if event['end_date'] < now:
        return False, "This event is over, and the final results will be announced soon!"
    return True, ""

def verify_site_admin():
    '''Verify current user is a site admin'''
    return g.user['role'] == 'siteAdmin'

def verify_site_helper():
    '''Verify current user is a site helper'''
    return g.user['role'] == 'siteHelper'

def verify_site_admin_or_helper(user_id):
    '''Verify current user is a site admin or helper'''
    return verify_site_admin() or verify_site_helper()

def verify_theme_roles(theme_id):
    '''Verify current user has any role in the theme'''
    theme_role = get_theme_role_by_user(theme_id, g.user['user_id'])
    return theme_role is not None

def verify_theme_admin(theme_id):
    '''Verify current user is a theme admin'''
    theme_role = get_theme_role_by_user(theme_id, g.user['user_id'])
    return theme_role is not None and theme_role['role'] == 'tAdmin'

def verify_site_admin_or_theme_admin(theme_id):
    '''Verify current user is a site admin or theme admin'''
    if not theme_id or theme_id == '0':
        return verify_site_admin()
    return verify_theme_admin(theme_id)

def verify_site_admin_or_theme_roles(theme_id):
    '''Verify current user is a site admin or theme admin or scrutineer'''
    if theme_id and theme_id != '0':
        return verify_theme_roles(theme_id)
    return verify_site_admin()
