from voting.database import Cursor

class Notification:
    def __init__(self, user_id, content, url):
        self.user_id = user_id
        self.content = content
        self.url = url

def create_notification(notification: Notification):
    with Cursor() as cursor:
        cursor.execute("""
            INSERT INTO notification (user_id, content, url)
            VALUES (%s, %s, %s)
        """, (notification.user_id, notification.content, notification.url))
        notification_id = cursor.lastrowid
    return notification_id

def create_notifications(notifications: list[Notification]):
    with Cursor() as cursor:
        cursor.executemany("""
            INSERT INTO notification (user_id, content, url)
            VALUES (%s, %s, %s)
        """, [(notification.user_id, notification.content, notification.url) for notification in notifications])

def get_notification_by_id(notification_id):
    with Cursor() as cursor:
        cursor.execute("""
            SELECT * FROM notification
            WHERE notification_id = %s
        """, (notification_id,))
        notification = cursor.fetchone()
    return notification

def get_all_notifications(user_id):
    with Cursor() as cursor:
        cursor.execute("""
            SELECT * FROM notification
            WHERE user_id = %s order by created_at desc
        """, (user_id,))
        notifications = cursor.fetchall()
    return notifications    

def get_unread_notifications(user_id):
    with Cursor() as cursor:
        cursor.execute("""
            SELECT * FROM notification
            WHERE user_id = %s AND has_read = 0 order by created_at desc
        """, (user_id,))
        notifications = cursor.fetchall()
    return notifications


def mark_notification_as_read(notification_id):
    with Cursor() as cursor:
        cursor.execute("""
            UPDATE notification
            SET has_read = 1, read_at = NOW()
            WHERE notification_id = %s
        """, (notification_id,))


def mark_all_notifications_as_read(user_id):
    with Cursor() as cursor:
        cursor.execute("""
            UPDATE notification
            SET has_read = 1, read_at = NOW()
            WHERE user_id = %s
        """, (user_id,))
        count = cursor.rowcount
    return count

