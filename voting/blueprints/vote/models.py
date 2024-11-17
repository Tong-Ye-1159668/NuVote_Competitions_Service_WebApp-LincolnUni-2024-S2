from voting.database import Cursor


def get_vote_by_id(vote_id):
    """
    Retrieve a vote by its ID, including the username of the voter
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                v.vote_id,
                c.event_id,
                v.candidate_id,
                v.voted_by,
                u.username AS voted_by_username,
                v.valid,
                v.voted_ip,
                v.voted_at
            FROM 
                votes v
                JOIN users u ON v.voted_by = u.user_id
                join candidates c on v.candidate_id = c.candidate_id
            WHERE 
                v.vote_id = %s
        """, (vote_id,))
        vote = cursor.fetchone()
    return vote


def create_vote(vote):
    """
    Create a new vote
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO votes (event_id, candidate_id, voted_by, voted_ip, valid)
            VALUES (%s, %s, %s, %s, %s)
        """, (
            vote['event_id'],
            vote['candidate_id'],
            vote['voted_by'],
            vote['voted_ip'],
            vote['valid']
        ))
        vote_id = cursor.lastrowid
    return vote_id


def set_vote_status(vote_id, valid):
    """
    Set the status of a vote (valid/invalid)
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE votes
            SET valid = %s
            WHERE vote_id = %s
        """, (valid, vote_id))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def get_daily_votes_by_event(event_id):
    """
    Retrieve the daily number of valid and invalid votes for an event
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                DATE(v.voted_at) AS vote_date,
                SUM(CASE WHEN v.valid = TRUE THEN 1 ELSE 0 END) AS valid_votes,
                SUM(CASE WHEN v.valid = FALSE THEN 1 ELSE 0 END) AS invalid_votes
            FROM 
                votes v, candidates c
            WHERE 
                v.candidate_id = c.candidate_id AND c.event_id = %s
            GROUP BY 
                DATE(v.voted_at)
            ORDER BY 
                vote_date
        """, (event_id,))
        daily_votes = cursor.fetchall()
    return daily_votes

def get_daily_valid_votes_by_candidate_and_event(event_id):
    """
    Retrieve the daily number of valid votes for each candidate in a specific event.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                DATE(v.voted_at) AS vote_date,
                v.candidate_id,
                c.name AS candidate_name,
                COUNT(*) AS valid_votes
            FROM 
                votes v
                JOIN candidates c ON v.candidate_id = c.candidate_id
            WHERE 
                c.event_id = %s AND v.valid = TRUE
            GROUP BY 
                DATE(v.voted_at), v.candidate_id
            ORDER BY 
                vote_date, v.candidate_id
        """, (event_id,))
        daily_valid_votes_by_candidate = cursor.fetchall()
    return daily_valid_votes_by_candidate


def get_votes_by_candidate_id(candidate_id):
    """
    Retrieve all votes for a specific candidate, ordered by time
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                v.vote_id,
                c.event_id,
                v.voted_by,
                v.valid,
                v.voted_ip,
                v.voted_at
            FROM 
                votes v, candidates c
            WHERE 
                v.candidate_id = c.candidate_id AND
                v.candidate_id = %s
            ORDER BY 
                v.voted_at DESC
        """, (candidate_id,))
        votes = cursor.fetchall()
    return votes


def get_votes_by_event_and_ip(event_id, voted_ip):
    """
    Retrieve all votes for a specific event and IP address, including the username of the voter
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                v.vote_id,
                c.event_id,
                v.candidate_id,
                v.voted_by,
                u.username AS voted_by_username,
                v.valid,
                v.voted_ip,
                v.voted_at
            FROM 
                votes v
                JOIN users u ON v.voted_by = u.user_id
                join candidates c on v.candidate_id = c.candidate_id
            WHERE 
                c.event_id = %s AND v.voted_ip = %s
        """, (event_id, voted_ip))
        votes = cursor.fetchall()
    return votes


def get_votes_group_by_ip_for_event(event_id):
    """
    Retrieve the number of total votes and valid votes per IP address for a specific event,
    ordered by the total number of votes in descending order
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                v.voted_ip,
                COUNT(*) AS total_votes,
                SUM(CASE WHEN v.valid = TRUE THEN 1 ELSE 0 END) AS valid_votes,
                count(distinct v.candidate_id) as distinct_candidates
            FROM 
                votes v, candidates c
            WHERE 
                v.candidate_id = c.candidate_id AND c.event_id = %s
            GROUP BY 
                v.voted_ip
            ORDER BY 
                total_votes DESC
        """, (event_id,))
        votes_by_ip = cursor.fetchall()
    return votes_by_ip


def invalidate_votes_by_ip_for_event(event_id, voted_ip):
    """
    Invalidate all votes for a specific IP address in an event
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE votes
            SET valid = FALSE
            WHERE event_id = %s AND voted_ip = %s
        """, (event_id, voted_ip))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def get_votes_by_event_and_user(event_id, user_id):
    """
    Retrieve all votes for a specific event and user, including the username of the voter
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                v.vote_id,
                c.event_id,
                v.candidate_id,
                v.voted_by,
                v.valid,
                v.voted_ip,
                v.voted_at
            FROM 
                votes v, candidates c
            WHERE 
                v.candidate_id = c.candidate_id AND c.event_id = %s AND v.voted_by = %s
        """, (event_id, user_id))
        votes = cursor.fetchall()
    return votes

def get_votes_by_filters(event_id, ip=None, valid=True, candidate_id=None):
    '''Retrieve votes based on event_id, ip, status, and candidate_id'''
    # CONVERT_TZ: jsonify force convert time to GMT no matter what time zone is
    query = """
        SELECT 
            v.vote_id,
            c.event_id,
            v.candidate_id,
            v.voted_by,
            u.username AS voted_by_username,
            u.first_name as voted_by_first_name,
            u.last_name as voted_by_last_name,
            v.valid,
            v.voted_ip,
            v.voted_at,
            c.name AS candidate_name
        FROM 
            votes v
            JOIN users u ON v.voted_by = u.user_id
            JOIN candidates c ON v.candidate_id = c.candidate_id
        WHERE 
            c.event_id = %s AND v.valid = %s
    """
    params = [event_id, valid]
    
    if ip:
        query += " AND v.voted_ip = %s"
        params.append(ip)
    
    if candidate_id:
        query += " AND v.candidate_id = %s"
        params.append(candidate_id)

    with Cursor(dictionary=True) as cursor:
        cursor.execute(query, params)
        votes = cursor.fetchall()
    return votes

def abandon_vote_by_id(vote_id):
    '''Set the status of a vote to invalid'''
    with Cursor() as cursor:
        cursor.execute("UPDATE votes SET valid = FALSE WHERE vote_id = %s AND valid = TRUE", (vote_id,))
        return cursor.rowcount

def abandon_votes_by_ids(vote_ids):
    '''Set the status of multiple votes to invalid'''
    placeholders = ', '.join(['%s'] * len(vote_ids))
    with Cursor() as cursor:
        cursor.execute(f"UPDATE votes SET valid = FALSE WHERE vote_id IN ({placeholders}) AND valid = TRUE", vote_ids)
        return cursor.rowcount

def abandon_votes_by_ip(ip):
    '''Set the status of all votes from an IP address to invalid'''
    with Cursor() as cursor:
        cursor.execute("UPDATE votes SET valid = FALSE WHERE voted_ip = %s AND valid = TRUE", (ip,))
        return cursor.rowcount

def get_recent_votes_by_user(user_id):
    """
    Retrieve the most recent votes by a specific user, including theme and event names.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT 
                v.*,
                t.theme_name,
                e.name AS event_name,
                c.name AS candidate_name
            FROM 
                votes v
                JOIN candidates c ON v.candidate_id = c.candidate_id
                JOIN events e ON c.event_id = e.event_id
                JOIN themes t ON e.theme_id = t.theme_id
            WHERE 
                v.voted_by = %s
            ORDER BY 
                v.voted_at DESC
        """, (user_id,))
        recent_votes = cursor.fetchall()
    return recent_votes

def get_location_votes_by_event(event_id):
    """
    Retrieve the number of votes for each location in an event
    """
    with Cursor() as cursor:
        cursor.execute("""
            SELECT u.user_city_name, u.user_city_latitude, u.user_city_longitude, COUNT(v.vote_id) AS vote_count
            FROM votes v
            JOIN users u ON v.voted_by = u.user_id
            WHERE v.event_id = %s
            GROUP BY u.user_city_name,u.user_city_latitude,u.user_city_longitude
            ORDER BY vote_count DESC
        """, (event_id,))
        location_votes = cursor.fetchall()
    return location_votes

