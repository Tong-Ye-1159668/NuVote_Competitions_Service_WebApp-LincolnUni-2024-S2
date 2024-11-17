from voting.config import MAX_LATEST_EVENTS
from voting.database import Cursor



class Event:
    def __init__(self, name, description, image, start_date, end_date, status, create_by, theme_id):
        self.name = name
        self.description = description
        self.image = image
        self.start_date = start_date
        self.end_date = end_date
        self.status = status
        self.create_by = create_by
        self.theme_id = theme_id


def get_event_by_id(event_id):
    """
    Retrieve an event by its ID
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT c.*, u.username, t.enable_location
            FROM events c
            JOIN users u ON c.create_by = u.user_id
            JOIN themes t ON c.theme_id = t.theme_id
            WHERE c.event_id = %s
        """, (event_id,))
        event = cursor.fetchone()
    return event


def create_event(event):
    """
    Create a new event
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO events (name, description, image, start_date, end_date, status, create_by, theme_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            event.name,
            event.description,
            event.image,
            event.start_date,
            event.end_date,
            event.status,
            event.create_by,
            event.theme_id
        ))
        event_id = cursor.lastrowid
    return event_id


def update_event(event_id, event):
    """
    Update event information based on event_id
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE events
            SET name = %s, description = %s, image = %s, start_date = %s, end_date = %s
            WHERE event_id = %s
        """, (
            event.name,
            event.description,
            event.image,
            event.start_date,
            event.end_date,
            event_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def update_event_status(event_id, status):
    """
    Update the status of an event
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE events
            SET status = %s
            WHERE event_id = %s
        """, (
            status,
            event_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def get_all_events():
    """
    Retrieve all events
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM events order by start_date
        """)
        events = cursor.fetchall()
    return events

def get_events_by_theme_id(theme_id):
    '''Get all events by theme id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM events WHERE theme_id = %s
        """, (theme_id,))
        events = cursor.fetchall()
    return events

def get_nondraft_events_by_theme_id(theme_id):
    '''Get all non-draft events by theme id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM events WHERE theme_id = %s AND status != 'draft' order by start_date desc
        """, (theme_id,))
        events = cursor.fetchall()
    return events

def delete_event(event_id):
    """
    Delete an event by its ID
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            DELETE FROM events
            WHERE event_id = %s
        """, (event_id,))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def get_latest_events():
    """
    Get latest 5 events order by start_date in descending order
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT e.*, t.theme_name
            FROM events e, themes t
            WHERE e.theme_id = t.theme_id AND e.status != 'draft'
            ORDER BY e.start_date DESC
            LIMIT %s
        """, (MAX_LATEST_EVENTS,))
        recent_events = cursor.fetchall()
    return recent_events

def update_event_image(event_id, event_image):
    '''Store the img_url in the database'''
    with Cursor() as cursor:
        cursor.execute("""
            UPDATE events
            SET image = %s
            WHERE event_id = %s
        """, (event_image, event_id))
        affected_rows = cursor.rowcount
    return affected_rows > 0

def get_most_popular_location(event_id):
    """Get the location with the highest number of voters for a specific event"""
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT l.latitude, l.longitude, l.location, COUNT(v.vote_id) AS vote_count
            FROM votes v
            JOIN users u ON v.voted_by = u.user_id
            JOIN locations l ON u.location_id = l.location_id
            WHERE v.event_id = %s
            GROUP BY l.location_id
            ORDER BY vote_count DESC
            LIMIT 1
        """, (event_id,))
        result = cursor.fetchone()  # Fetch the top result (location with most votes)
    return result

