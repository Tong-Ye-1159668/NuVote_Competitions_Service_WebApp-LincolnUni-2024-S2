from voting.database import Cursor


class Announcement:
    def __init__(self, title, content, end_at, status, created_by):
        self.title = title
        self.content = content
        self.end_at = end_at
        self.status = status
        self.created_by = created_by


def create_announcement(announcement):
    """
    Create a new announcement.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO announcements (title, content, end_at, status, created_by)
            VALUES (%s, %s, %s, %s, %s)
        """, (
            announcement.title,
            announcement.content,
            announcement.end_at,
            announcement.status,
            announcement.created_by
        ))
        announcement_id = cursor.lastrowid
    return announcement_id


def get_announcement_by_id(announcement_id):
    """
    Get an announcement by its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM announcements
            WHERE announcement_id = %s
        """, (announcement_id,))
        announcement = cursor.fetchone()
    return announcement


def get_all_announcements():
    """
    Get all announcements, ordered by created_at descending.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM announcements
            ORDER BY created_at DESC
        """)
        announcements = cursor.fetchall()
    return announcements


def get_active_announcements():
    """
    Get all active announcements where status is 'active' and end_at is greater than the current time.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM announcements
            WHERE status = 'active' AND end_at > NOW()
            ORDER BY created_at DESC
        """)
        active_announcements = cursor.fetchall()
    return active_announcements


def get_latest_active_announcement():
    """
    Get the latest active announcement.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM announcements
            WHERE status = 'active' AND end_at > NOW()
            ORDER BY created_at DESC
            LIMIT 1
        """)
        latest_active_announcement = cursor.fetchone()
    return latest_active_announcement


def update_announcement(announcement_id, announcement):
    """
    Update an announcement based on its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE announcements
            SET title = %s, content = %s, end_at = %s, status = %s
            WHERE announcement_id = %s
        """, (
            announcement.title,
            announcement.content,
            announcement.end_at,
            announcement.status,
            announcement_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def delete_announcement(announcement_id):
    """
    Delete an announcement by its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            DELETE FROM announcements
            WHERE announcement_id = %s
        """, (announcement_id,))
        affected_rows = cursor.rowcount
    return affected_rows > 0
