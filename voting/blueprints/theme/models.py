from voting.config import DEFAULT_EVENT_IMAGE
from voting.database import Cursor


def get_theme_by_id(theme_id):
    '''Retrieve all users'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username FROM themes t, users u WHERE t.created_by = u.user_id AND t.theme_id = %s''', (theme_id,)
        )
        theme = cursor.fetchone()
    return theme

def get_all_themes_created_by_user(user_id):
    '''Retrieve all themes created by a user'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username FROM themes t, users u WHERE t.created_by = u.user_id AND t.created_by = %s order by t.created_at desc''', (user_id,)
        )
        themes = cursor.fetchall()
    return themes

def create_theme(theme_name, theme_description, created_by, enable_location):
    '''Create a new theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''INSERT INTO themes (theme_name, theme_description, created_by, enable_location) VALUES (%s, %s, %s, %s)''', (theme_name, theme_description, created_by, enable_location)
        )
        theme_id = cursor.lastrowid
    return theme_id

def approve_theme(theme_id, approved_by, accepted):
    '''Approve a theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''UPDATE themes SET approved = 1, approved_by = %s, approved_at=now(), accepted = %s WHERE theme_id = %s''', (approved_by, accepted, theme_id)
        )
        updated_rows = cursor.rowcount
    return updated_rows

def delete_theme(theme_id):
    '''Delete a theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''DELETE FROM themes WHERE theme_id = %s and approved = 0''', (theme_id,)
        )
        deleted_rows = cursor.rowcount
    return deleted_rows

def get_all_themes_managed_by_me(user_id):
    '''Retrieve all themes managed by me'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username 
            FROM themes t
            inner join theme_roles tr on t.theme_id=tr.theme_id 
            inner join users u on t.created_by=u.user_id
            WHERE tr.user_id = %s 
            AND t.approved = 1 and t.accepted = 1 
            order by t.created_at desc''', (user_id,)
        )
        themes = cursor.fetchall()
    return themes

def get_all_pending_themes():
    '''Retrieve all themes pending approval'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username FROM themes t, users u WHERE t.created_by = u.user_id AND t.approved = 0 order by t.created_at desc'''
        )
        themes = cursor.fetchall()
    return themes

def get_all_approved_themes():
    '''Retrieve all approved themes'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username FROM themes t, users u 
            WHERE t.created_by = u.user_id AND t.approved = 1 order by t.created_at desc'''
        )
        themes = cursor.fetchall()
    return themes

def get_all_accepted_themes():
    '''Retrieve all accepted themes'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username FROM themes t, users u 
            WHERE t.created_by = u.user_id AND t.approved = 1 and t.accepted = 1 order by t.created_at desc'''
        )
        themes = cursor.fetchall()
    return themes

def get_all_accepted_themes_with_images():
    '''Retrieve all accepted themes with images'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT t.*, u.username as created_by_username, ifnull(i.image, %s) as image
            FROM themes t 
            INNER JOIN users u ON t.created_by = u.user_id
            LEFT JOIN (
                SELECT e.theme_id, e.image
                FROM (
                    SELECT e.*, ROW_NUMBER() OVER (PARTITION BY e.theme_id ORDER BY e.created_at) as rn
                    FROM events e
                    WHERE e.image IS NOT NULL
                ) as e
                WHERE e.rn = 1
            ) i ON i.theme_id = t.theme_id
            WHERE t.approved = 1 AND t.accepted = 1 ORDER BY t.created_at DESC''', (DEFAULT_EVENT_IMAGE,)
        )
        themes = cursor.fetchall()
    return themes

# ==================================================
# Theme Roles
# ==================================================

def get_theme_roles_by_theme_id(theme_id):
    '''Retrieve all roles of a theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT r.*, u.username as user_name FROM theme_roles r, users u WHERE r.theme_id = %s and r.user_id = u.user_id''', (theme_id,)
        )
        theme_roles = cursor.fetchall()
    return theme_roles

def add_role_to_theme(theme_id, user_id, role, created_by):
    '''Add a role to a theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''INSERT INTO theme_roles (theme_id, user_id, role, created_by) VALUES (%s, %s, %s, %s)''', (theme_id, user_id, role, created_by)
        )
        theme_role_id = cursor.lastrowid
    return theme_role_id

def remove_role_from_theme(theme_role_id):
    '''Remove a role from a theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''DELETE FROM theme_roles WHERE theme_role_id = %s''', (theme_role_id,)
        )
        deleted_rows = cursor.rowcount
    return deleted_rows

def update_theme_role(theme_role_id, role):
    '''Update a theme role'''
    with Cursor() as cursor:
        cursor.execute(
            '''UPDATE theme_roles SET role = %s WHERE theme_role_id = %s''', (role, theme_role_id)
        )
        updated_rows = cursor.rowcount
    return updated_rows

def get_theme_role_by_user(theme_id, user_id):
    '''Get the role of a user in a theme'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * FROM theme_roles WHERE theme_id = %s AND user_id = %s''', (theme_id, user_id)
        )
        theme_role = cursor.fetchone()
    return theme_role

def get_theme_roles(user_id):
    '''Get the roles of a user for all themes'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * FROM theme_roles WHERE user_id = %s''', (user_id,)
        )
        theme_roles = cursor.fetchall()
    return theme_roles

def get_theme_role_by_id(theme_role_id):
    '''Get a theme role by id'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * FROM theme_roles WHERE theme_role_id = %s''', (theme_role_id,)
        )
        theme_role = cursor.fetchone()
    return theme_role

def get_users_without_theme_role(theme_id):
    '''Get users without a theme role'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT u.user_id, u.username FROM users u WHERE u.user_id NOT IN (SELECT user_id FROM theme_roles WHERE theme_id = %s)''', (theme_id,)
        )
        users = cursor.fetchall()
    return users

