from voting.database import Cursor

def get_candidate_by_id(candidate_id):
    """
    Retrieve a candidate by their ID, including event and location details
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT a.*, b.name AS event_name, c.*
            FROM candidates a
            JOIN events b ON a.event_id = b.event_id
            LEFT JOIN locations c ON a.location_id = c.location_id
            WHERE a.candidate_id = %s
        """, (candidate_id,))
        candidate = cursor.fetchone()
        
    return candidate


def create_candidate(candidate):
    """
    Create a new candidate
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO candidates (name, description, image, location_id, event_id, author, create_by)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """, (
            candidate['name'],
            candidate['description'],
            candidate['image'],
            candidate['location_id'],
            candidate['event_id'],
            candidate['author'],
            candidate['create_by']
        ))
        candidate_id = cursor.lastrowid
    return candidate_id


def update_candidate(candidate):
    """
    Update candidate information based on candidate_id
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE candidates
            SET name = %s, description = %s, image = %s, author = %s,location_id = %s
            WHERE candidate_id = %s
        """, (
            candidate['name'],
            candidate['description'],
            candidate['image'],
            candidate['author'],
            candidate['location_id'],  
            candidate['candidate_id']
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def get_all_candidates():
    """
    Retrieve all candidates, ordered by creation time
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM candidates
            ORDER BY created_at
        """)
        candidates = cursor.fetchall()
    return candidates


def get_candidates_by_event_id(event_id):
    """
    Retrieve candidates by their event_id, including location details
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT c.*, l.location, l.latitude, l.longitude
            FROM candidates c
            LEFT JOIN locations l ON c.location_id = l.location_id
            WHERE c.event_id = %s
            ORDER BY c.created_at
        """, (event_id,))
        candidates = cursor.fetchall()
    return candidates



def get_candidates_with_valid_votes(event_id):
    """
    Retrieve all candidates in an event with their total valid votes
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                c.candidate_id,
                c.name,
                c.description,
                c.image,
                c.author,
                c.created_at,
                IFNULL(v.vote_count, 0) AS vote_count
            FROM 
                candidates c
                LEFT JOIN (
                    SELECT candidate_id, COUNT(*) AS vote_count 
                    FROM votes 
                    WHERE event_id = %s AND valid = 1
                    GROUP BY candidate_id
                ) v ON c.candidate_id = v.candidate_id
            WHERE 
                c.event_id = %s
            ORDER BY 
                c.created_at DESC
        """, (event_id, event_id))

        candidates = cursor.fetchall()
    return candidates


def get_candidates_with_votes_percentage(event_id):
    """
    Retrieve all candidates in an event with their total valid votes and vote count percentage
    """
    with Cursor(dictionary=True) as cursor:
        # Calculate total valid votes for the event
        cursor.execute("""
            SELECT COUNT(*) AS total_votes
            FROM votes 
            WHERE event_id = %s AND valid = 1
        """, (event_id,))
        total_votes = cursor.fetchone()['total_votes']

        # Retrieve candidates with their valid vote counts and calculate percentage
        cursor.execute("""
            SELECT 
                c.candidate_id,
                c.name,
                c.description,
                c.image,
                c.author,
                c.created_at,
                IFNULL(v.vote_count, 0) AS vote_count,
                IFNULL((v.vote_count / %s) * 100, 0) AS vote_percentage
            FROM 
                candidates c
                LEFT JOIN (
                    SELECT candidate_id, COUNT(*) AS vote_count 
                    FROM votes 
                    WHERE event_id = %s AND valid = 1
                    GROUP BY candidate_id
                ) v ON c.candidate_id = v.candidate_id
            WHERE 
                c.event_id = %s
            ORDER BY 
                vote_percentage DESC
        """, (total_votes, event_id, event_id))

        candidates = cursor.fetchall()
    return candidates


def get_candidates_and_votes(event_id, user_id):
    """
    Retrieve all candidates in an event with their total votes,
    check if the specified user has voted for each candidate,
    and include location details.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                c.candidate_id,
                c.name,
                c.description,
                c.image,
                c.author,
                c.created_at,
                IFNULL(v.vote_count, 0) AS vote_count,
                IF(EXISTS(
                    SELECT 1 FROM votes v2 
                    WHERE v2.candidate_id = c.candidate_id AND v2.voted_by = %s
                ), 1, 0) AS has_voted,
                l.location,
                l.latitude,
                l.longitude
            FROM 
                candidates c
                LEFT JOIN (
                    SELECT candidate_id, COUNT(*) AS vote_count 
                    FROM votes 
                    GROUP BY candidate_id
                ) v ON c.candidate_id = v.candidate_id
                LEFT JOIN locations l ON c.location_id = l.location_id
            WHERE 
                c.event_id = %s
            ORDER BY 
                c.created_at DESC
        """, (user_id, event_id))

        candidates = cursor.fetchall()
    return candidates

def delete_candidate(candidate_id):
    """
    Delete a candidate by its ID
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            DELETE FROM candidates
            WHERE candidate_id = %s
        """, (candidate_id,))
        affected_rows = cursor.rowcount
    return affected_rows > 0

def get_location_by_name(location, latitude, longitude ):
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

def get_most_popular_candidate(event_id):
    """
    Retrieve the most popular candidate in an event
    """
    with Cursor as cursor:
        cursor.execute("""
            SELECT 
                c.candidate_id, 
                c.name, 
                l.latitude, 
                l.longitude, 
                COUNT(v.candidate_id) AS vote_count
            FROM 
                votes v
            JOIN 
                candidates c ON v.candidate_id = c.candidate_id
                JOIN 
                locations l ON c.location_id = l.location_id
            WHERE 
                v.event_id = %s
            GROUP BY 
                c.candidate_id, c.name, l.latitude, l.longitude
            ORDER BY 
                vote_count DESC
            LIMIT 1
        """, (event_id,))
        most_popular_candidate = cursor.fetchone()
    return most_popular_candidate