from voting.config import DEFAULT_VOTE_VALID
from voting.database import Cursor
from voting.blueprints.event.models import get_all_events, get_event_by_id
from voting.blueprints.canditate.models import get_candidates_by_event_id
import random
import datetime

# Define an IP_TEMPLATE for generating IP addresses
IP_TEMPLATE = '10.{}.{}.{}'


def create_votes(votes):
    """
    Create multiple new votes
    """
    # Prepare the query for batch insertion
    insert_query = """
        INSERT INTO votes (event_id, candidate_id, voted_by, valid, voted_ip, voted_at)
        VALUES (%s, %s, %s, %s, %s, %s)
    """

    # Prepare the values for each vote in a list of tuples
    values = [
        (
            vote['event_id'],
            vote['candidate_id'],
            vote['voted_by'],
            DEFAULT_VOTE_VALID,
            vote['voted_ip'],
            vote['created_at']
        )
        for vote in votes
    ]

    with Cursor() as cursor:
        # Execute batch insertion
        cursor.executemany(insert_query, values)


def select_user_ids_not_voted(event_id):
    """
    Select user ids that have not voted yet
    """
    with Cursor() as cursor:
        cursor.execute("""
            SELECT user_id
            FROM users u
            WHERE user_id NOT IN (
                SELECT voted_by
                FROM votes v, candidates c
                WHERE v.candidate_id=c.candidate_id and c.event_id = %s
            ) and u.role='voter'
        """, (event_id,))
        user_ids = cursor.fetchall()
    return user_ids


def random_date(start, end):
    """
    Generate a random datetime between `start` and `end` dates.
    """
    # Calculate the total number of seconds between start and end
    if datetime.datetime.now() < end:
        end = datetime.datetime.now()
    delta = end - start
    int_delta = int(delta.total_seconds())

    # Generate a random number of seconds to add to the start date
    random_seconds = random.randint(0, int_delta)

    # Add the random seconds to the start date to get the random date
    random_date = start + datetime.timedelta(seconds=random_seconds)

    return random_date


def random_weights(length):
    """
    Generate random weights that sum up to 1
    """
    return [random.random() for _ in range(length)]

# Generate votes for specific event (associated by event_id), default number of legal votes is 50, illegal votes is 30.
def generate_votes_for_event(event_id, legal_votes_count=50, illegal_votes_count=30):
    """ Generate votes for an event, legal and illegal votes"""
    # get details of event by event_id
    event = get_event_by_id(event_id)
    # ensure the event exist. 
    if not event:
        print(f'event with id {event_id} not found')
        return
    # ensure event status not in draft and in_plan
    if event['status'] in ('draft', 'in_plan'):
        print(f'event with id {event_id} is {event["status"]}, ignore')
        return
    # future event
    if event['start_date'] > datetime.datetime.now():
        print(f'event with id {event_id} is {event["start_date"]}, ignore')
        return
    # ensure current date is between start_date and end_date
    if not (event['start_date'] and event['end_date']):
        print(f'event with id {event_id} has no start and end dates')
        return

    # get user ids who haven't voted yet
    user_ids = select_user_ids_not_voted(event_id)
    # check if there are enough users to generate votes, if not, return, if yes, continute.
    if len(user_ids) < legal_votes_count + illegal_votes_count:
        print(f'not enough users to generate votes for event {event_id}')
        return

    # get details of candidates by event_id
    candidates = get_candidates_by_event_id(event_id)

    # generate 50 legal votes and append to votes
    votes = []
    # legal votes will be distributed to all candidates
    weights = random_weights(len(candidates))
    for i in range(legal_votes_count):
        user_id = user_ids[i]['user_id']
        # randomly choose candidates
        candidate = random.choices(candidates, weights=weights)[0]
        # given an ip align with IP_template
        ip = IP_TEMPLATE.format(event_id, i//256, i % 256)
        # assign a random date from start date to end date
        created_at = random_date(event['start_date'], event['end_date'])
        # merge information for a vote
        vote = {
            'event_id': event_id,
            'candidate_id': candidate['candidate_id'],
            'voted_by': user_id,
            'voted_ip': ip,
            'created_at': created_at
        }
        # print(str(vote))
        votes.append(vote)
    
    # Pick 3 candidates to cheat, with 3 different ips
    candidates_cheating = random.sample(candidates, k=3)
    # illegal votes will be distributed to 3 random selected candidates
    weights_chating = random_weights(3)
    # create 3 different ip addresses
    ips = [IP_TEMPLATE.format(event_id, 200, random.randint(0, 255)) for _ in range(3)]
    
    # generate 30 illegal votes and append to votes
    for i in range(illegal_votes_count):
        # ensure that the illegal votes are associated with different user IDs than those used for legal votes
        user_id = user_ids[legal_votes_count + i]['user_id']
        candidate = random.choices(candidates_cheating, weights=weights_chating)[0]
        created_at = random_date(event['start_date'], event['end_date'])
        vote = {
            'event_id': event_id,
            'candidate_id': candidate['candidate_id'],
            'voted_by': user_id,
            'voted_ip': random.choice(ips),
            'created_at': created_at
        }
        votes.append(vote)
    if votes:
        # insert created votes to the main votes table by calling create_votes function
        create_votes(votes)

    # print out success message
    print(f'Generated {legal_votes_count} legal and {illegal_votes_count} illegal votes for event {event_id}')

def generate_votes_for_all_events(legal_votes_count=50, illegal_votes_count=30):
    """Generate all votes for all events, legal and illegal votes"""
    events = get_all_events()
    for event in events:
        generate_votes_for_event(event['event_id'], legal_votes_count, illegal_votes_count)


if __name__ == "__main__":
    # generate_votes_for_event(1)
    generate_votes_for_all_events()
