from flask_hashing import Hashing

from voting.config import DEFAULT_USER_STATUS, DEFAULT_PROFILE_IMAGE, DEFAULT_PASSWORD_SALT, MAX_LATEST_VOTE_USERS
from voting.database import Cursor
from voting.utility import get_current_datetime

PASSWORD_SALT = DEFAULT_PASSWORD_SALT
hashing = Hashing()


class User:
    def __init__(self, username, first_name, last_name, email, description, password, role):
        self.username = username
        self.first_name = first_name
        self.last_name = last_name
        self.email = email
        self.description = description
        self.password = password
        self.role = role


def get_users():
    '''Retrieve all users'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * 
            FROM users u 
            ORDER BY u.user_id'''
        )
        users = cursor.fetchall()
    return users


def get_user_by_id(user_id):
    '''Retrieve user information based on user_id'''

    # We need all the user info for the user, so we can display it on the profile page
    with Cursor() as cursor:
        cursor.execute(
            'SELECT u.*, l.* FROM users u left join locations l on u.location_id = l.location_id WHERE u.user_id = %s',
            (user_id,))
        user = cursor.fetchone()
    # Set default Profile Image
    return user


def get_user_by_username(username):
    '''Retrieve user information based on username'''

    # We need all the account info for the user, so we can display it on the profile page
    with Cursor() as cursor:
        cursor.execute(
            'SELECT * FROM users WHERE username = %s',
            (username,))
        user = cursor.fetchone()
    # Set default Profile Image
    return user


def get_password_by_username(username):
    '''Retrieve user information based on username'''

    # We need all the account info for the user, so we can display it on the profile page
    with Cursor() as cursor:
        cursor.execute(
            'SELECT password_hash FROM users WHERE username = %s',
            (username,))
        user = cursor.fetchone()
    # Set default Profile Image
    return user


def get_user_by_email(email):
    '''Retrieve user information based on email'''

    # We need all the account info for the user, so we can display it on the profile page
    with Cursor() as cursor:
        cursor.execute(
            'SELECT user_id, username, first_name, last_name, email,description, profile_image, '
            'role, status, created_at FROM users WHERE email = %s',
            (email,))
        user = cursor.fetchone()
    # Set default Profile Image
    return user


def create_user(user: User):
    '''Create a new user'''
    with Cursor() as cursor:
        cursor.execute(
            'INSERT INTO users ('
            'username, '
            'first_name, '
            'last_name, '
            'email, '
            'description, '
            'profile_image,'
            'password_hash, '
            'role, '
            'status,'
            'created_at) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)',
            (
                user.username,
                user.first_name,
                user.last_name,
                user.email,
                user.description,
                DEFAULT_PROFILE_IMAGE,
                hashing.hash_value(user.password, PASSWORD_SALT),
                user.role,
                DEFAULT_USER_STATUS,
                get_current_datetime(),))
    return get_user_by_username(user.username)


def update_user_password_by_id(user_id, password):
    '''Update user password based on user_id'''
    hashed_password = hashing.hash_value(password, PASSWORD_SALT)
    with Cursor() as cursor:
        cursor.execute('UPDATE users SET password_hash = %s WHERE user_id = %s', (hashed_password, user_id))


def is_user_password_valid_by_id(user_id, password):
    '''Check if user password is valid based on user_id'''
    with Cursor() as cursor:
        cursor.execute('SELECT password_hash FROM users WHERE user_id = %s', (user_id,))
        user = cursor.fetchone()
    return user['password_hash'] == hashing.hash_value(password, PASSWORD_SALT)


def is_user_password_valid_by_username(username, password):
    '''Check if user password is valid based on username'''
    with Cursor() as cursor:
        cursor.execute('SELECT password_hash FROM users WHERE username = %s', (username,))
        user = cursor.fetchone()
    return user['password_hash'] == hashing.hash_value(password, PASSWORD_SALT)


def update_user_status(user_id, status):
    '''Update user status'''
    with Cursor() as cursor:
        cursor.execute('UPDATE users SET status = %s WHERE user_id = %s', (status, user_id))
    return get_user_by_id(user_id)


def update_user_role(user_id, role):
    '''Update user role'''
    with Cursor() as cursor:
        cursor.execute('UPDATE users SET role = %s WHERE user_id = %s', (role, user_id))
    return get_user_by_id(user_id)


def check_username_exists(username):
    """Check if username exists"""
    with Cursor() as cursor:
        cursor.execute(
            "SELECT COUNT(*) FROM users WHERE username = %s", (username,))
        exists = cursor.fetchone()[0] > 0
    return exists


def check_email_exists(email):
    """Check if email exists"""
    with Cursor() as cursor:
        cursor.execute(
            "SELECT COUNT(*) FROM users WHERE email = %s", (email,))
        exists = cursor.fetchone()[0] > 0
    return exists


def update_user(user):
    """
    Update user information based on user_id
    """
    with Cursor() as cursor:
        cursor.execute("""
            UPDATE users
            SET email = %s, first_name = %s, last_name = %s, description = %s, status = %s, role = %s, location_id = %s, display_location = %s, user_city_name = %s, user_city_latitude = %s, user_city_longitude = %s
            WHERE user_id = %s
        """, (
            user['email'],
            user['first_name'],
            user['last_name'],
            user['description'],
            user['status'],
            user['role'],
            user['location_id'],
            user['display_location'],
            user['user_city_name'],
            user['user_city_latitude'],
            user['user_city_longitude'],
            user['user_id']
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def update_profile_image(user_id, profile_image):
    """
    Update the profile image URL for a user based on user_id.
    """
    with Cursor() as cursor:
        cursor.execute("""
            UPDATE users
            SET profile_image = %s
            WHERE user_id = %s
        """, (profile_image, user_id))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def get_latest_voted_users():
    """
    Get the latest voted users
    """
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT u.* 
            FROM users u, votes v where u.user_id = v.voted_by and u.status='active'
            ORDER BY v.voted_at desc limit %s''', (MAX_LATEST_VOTE_USERS,)
        )
        users = cursor.fetchall()
    return users


def get_users_by_role(role):
    '''Retrieve users based on role'''
    with Cursor() as cursor:
        cursor.execute('SELECT * FROM users WHERE role = %s', (role,))
        users = cursor.fetchall()
    return users


def search_users(query, role, status):
    '''Search users based on query, role, and status'''
    with Cursor() as cursor:
        sql = '''
            SELECT * FROM users WHERE 1=1
        '''
        params = []
        if query:
            sql += ' AND (username LIKE %s OR email LIKE %s OR first_name LIKE %s OR last_name LIKE %s)'
            params.append(f'%{query}%')
            params.append(f'%{query}%')
            params.append(f'%{query}%')
            params.append(f'%{query}%')
        if role:
            sql += ' AND role = %s'
            params.append(role)
        if status:
            sql += ' AND status = %s'
            params.append(status)
        sql += ' ORDER BY user_id DESC'
        cursor.execute(sql, params)
        users = cursor.fetchall()
    return users


def get_location_by_name(location, latitude, longitude):
    '''Retrieve location information based on location name'''
    with Cursor() as cursor:
        cursor.execute(
            'SELECT * FROM locations WHERE location = %s and latitude = %s and longitude = %s',
            (location, latitude, longitude))
        location = cursor.fetchone()
    return location


def create_location(location, latitude, longitude):
    '''Create a new location'''
    with Cursor() as cursor:
        cursor.execute(
            'INSERT INTO locations (location, latitude, longitude) VALUES (%s, %s, %s)',
            (location, latitude, longitude))
        location_id = cursor.lastrowid
    return location_id


# ==============================================================
# Banned Users
# ==============================================================

def get_banned_users_by_theme_id(theme_id):
    '''Get banned users by theme id'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT b.*, u.username as username, u1.username as banned_by_username, u2.username as revoked_by_username, coalesce(c.appeal_count, 0) as appeal_count
            FROM banned_users b
             left join users u on b.user_id=u.user_id
             left join users u1 on b.banned_by=u1.user_id
             left join users u2 on b.revoked_by=u2.user_id 
             left join (select count(*) as appeal_count, ban_id from appeals a group by a.ban_id) c on b.ban_id=c.ban_id
             WHERE b.theme_id = %s
             order by b.banned_at desc''', (theme_id,)
        )
        users = cursor.fetchall()
    return users


def get_ban_by_id(ban_id):
    '''Get a ban by id'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * FROM banned_users WHERE ban_id = %s''', (ban_id,)
        )
        ban = cursor.fetchone()
    return ban


def ban_user(theme_id, user_id, banned_by, banned_reason):
    '''Ban a user'''
    with Cursor() as cursor:
        cursor.execute(
            '''INSERT INTO banned_users (theme_id, user_id, banned_by, banned_reason) VALUES (%s, %s, %s, %s)''',
            (theme_id, user_id, banned_by, banned_reason)
        )
        ban_id = cursor.lastrowid
    return ban_id


def revoke_ban(ban_id, revoked_by):
    '''Revoke a ban'''
    with Cursor() as cursor:
        cursor.execute(
            '''UPDATE banned_users SET revoked = 1, revoked_by = %s, revoked_at = now() WHERE ban_id = %s''',
            (revoked_by, ban_id)
        )
        updated_rows = cursor.rowcount
    return updated_rows


def get_ban_by_theme_and_user(theme_id, user_id):
    '''Get a ban by theme and user'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * FROM banned_users WHERE theme_id = %s AND user_id = %s and revoked = 0''', (theme_id, user_id)
        )
        ban = cursor.fetchone()
    return ban


def create_appeal(ban_id, appealed_reason):
    '''Create an appeal'''
    with Cursor() as cursor:
        cursor.execute(
            '''INSERT INTO appeals (ban_id, appealed_reason) VALUES (%s, %s)''', (ban_id, appealed_reason)
        )
        appeal_id = cursor.lastrowid
    return appeal_id


def get_appeal_by_id(appeal_id):
    '''Get an appeal by id'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT * FROM appeals WHERE appeal_id = %s order by appealed_at desc''', (appeal_id,)
        )
        appeal = cursor.fetchone()
    return appeal


def get_appeals_by_ban_id(ban_id):
    '''Get appeals by ban id'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT a.*, u.username as processed_by_username FROM appeals a
            left join users u on a.processed_by=u.user_id WHERE ban_id = %s''', (ban_id,)
        )
        appeals = cursor.fetchall()
    return appeals


def approve_appeal(appeal_id, approved_by):
    '''Approve an appeal'''
    with Cursor() as cursor:
        cursor.execute(
            '''UPDATE appeals SET processed = 1, processed_by = %s, processed_at = now(), accepted = 1 WHERE appeal_id = %s''',
            (approved_by, appeal_id)
        )
        updated_rows = cursor.rowcount
    return updated_rows


def reject_appeal(appeal_id, rejected_by):
    '''Reject an appeal'''
    with Cursor() as cursor:
        cursor.execute(
            '''UPDATE appeals SET processed = 1, processed_by = %s, processed_at = now(), accepted = 0 WHERE appeal_id = %s''',
            (rejected_by, appeal_id)
        )
        updated_rows = cursor.rowcount
    return updated_rows


def revoke_ban(ban_id, revoked_by):
    '''Revoke a ban'''
    with Cursor() as cursor:
        cursor.execute(
            '''UPDATE banned_users SET revoked = 1, revoked_by = %s, revoked_at = now() WHERE ban_id = %s''',
            (revoked_by, ban_id)
        )
        updated_rows = cursor.rowcount
    return updated_rows


def get_banned_users():
    '''Get banned users'''
    with Cursor() as cursor:
        cursor.execute('''SELECT b.*, if(b.theme_id=0, '[Site Wide]', t.theme_name) as theme_name, u1.username, u2.username as banned_by_username, u3.username as revoked_by_username FROM banned_users b
                       left join users u1 on b.user_id=u1.user_id
                       left join users u2 on b.banned_by=u2.user_id
                       left join users u3 on b.revoked_by=u3.user_id
                       left join themes t on b.theme_id=t.theme_id
                       WHERE 1=1 order by b.theme_id, b.user_id, b.banned_at desc''')
        users = cursor.fetchall()
    return users


def search_banned_users(theme_id, user_id, revoked):
    '''Search banned users'''
    with Cursor() as cursor:
        sql = '''
            SELECT b.*, if(b.theme_id=0, '[Site Wide]', t.theme_name) as theme_name, u1.username, u2.username as banned_by_username, u3.username as revoked_by_username, coalesce(c.appeal_count, 0) as appeal_count
            FROM banned_users b
            left join users u1 on b.user_id=u1.user_id
            left join users u2 on b.banned_by=u2.user_id
            left join users u3 on b.revoked_by=u3.user_id
            left join themes t on b.theme_id=t.theme_id
            left join (select count(*) as appeal_count, ban_id from appeals a group by a.ban_id) c on b.ban_id=c.ban_id
            WHERE 1=1
        '''
        params = []
        if theme_id:
            sql += ' AND b.theme_id = %s'
            params.append(theme_id)
        if user_id:
            sql += ' AND b.user_id = %s'
            params.append(user_id)
        if revoked is not None and revoked != '':
            sql += ' AND b.revoked = %s'
            params.append(revoked)
        sql += ' ORDER BY b.theme_id, b.user_id, b.banned_at DESC'
        cursor.execute(sql, params)
        users = cursor.fetchall()
    return users


def get_bans_by_user_id(user_id):
    '''Get bans by user_id'''
    with Cursor() as cursor:
        cursor.execute('''SELECT b.*, if(b.theme_id=0, '[Site Wide]', t.theme_name) as theme_name, u2.username as banned_by_username, u3.username as revoked_by_username, coalesce(c.appeal_count, 0) as appeal_count
                       FROM banned_users b
                       left join users u2 on b.banned_by=u2.user_id
                       left join users u3 on b.revoked_by=u3.user_id
                       left join themes t on b.theme_id=t.theme_id
                       left join (select count(*) as appeal_count, ban_id from appeals a group by a.ban_id) c on b.ban_id=c.ban_id  
                       WHERE b.user_id = %s
                       order by b.theme_id, b.banned_at desc''', (user_id,))
        bans = cursor.fetchall()
    return bans


def get_power_user():
    '''get all power users'''
    with Cursor() as cursor:
        cursor.execute('''
            SELECT * FROM users WHERE role != 'voter'
        ''')
        users = cursor.fetchall()
    return users
