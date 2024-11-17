from flask import url_for

from voting.database import Cursor
from voting.signals import ticket_created, ticket_assigned, ticket_processed, ticket_commented, ticket_closed


class Ticket:
    def __init__(self, subject, content, status, created_by, created_at):
        self.subject = subject
        self.content = content
        self.status = status
        self.created_by = created_by
        self.created_at = created_at


def create_ticket(ticket):
    """
    Create a new ticket.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO tickets (subject, content, created_by)
            VALUES (%s, %s, %s)
        """, (
            ticket.subject,
            ticket.content,
            ticket.created_by
        ))
        ticket_id = cursor.lastrowid
    return ticket_id


def get_ticket_by_id(ticket_id):
    """
    Get a ticket by its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM tickets_management_view
            WHERE ticket_id = %s
        """, (ticket_id,))
        ticket = cursor.fetchone()
    return ticket


def get_all_tickets():
    """
    Get all tickets, ordered by created_at descending.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM tickets_management_view
            ORDER BY created_at DESC
        """)
        tickets = cursor.fetchall()
    return tickets


def get_all_my_tickets(user_id):
    """
    Get all tickets, ordered by created_at descending.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM tickets_management_view
            WHERE created_by = %s
            ORDER BY created_at DESC
        """, (user_id,))
        tickets = cursor.fetchall()
    return tickets


def get_new_tickets():
    """
    Get all active tickets where status is not 'closed'.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM tickets_management_view
            WHERE status = 'new'
            ORDER BY created_at DESC
        """)
        active_tickets = cursor.fetchall()
    return active_tickets


def get_open_tickets():
    """
    Get all active tickets where status is not 'closed'.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM tickets_management_view
            WHERE status = 'open'
            ORDER BY created_at DESC
        """)
        active_tickets = cursor.fetchall()
    return active_tickets


def get_closed_tickets():
    """
    Get all active tickets where status is not 'closed'.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM tickets_management_view
            WHERE status = 'closed'
            ORDER BY created_at DESC
        """)
        active_tickets = cursor.fetchall()
    return active_tickets


def assign_ticket(ticket_id, updated_by, updated_at, assigned_to, status='open'):
    """
    Update a ticket based on its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE tickets
            SET assign_to = %s, updated_by = %s, updated_at = %s, status = %s
            WHERE ticket_id = %s
        """, (
            assigned_to,
            updated_by,
            updated_at,
            status,
            ticket_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0

def unassign_ticket(ticket_id, updated_by, updated_at):
    """
    Update a ticket based on its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE tickets
            SET assign_to = NULL, updated_by = %s, updated_at = %s, status = 'new'
            WHERE ticket_id = %s
        """, (
            updated_by,
            updated_at,
            ticket_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def update_ticket(ticket_id, ticket):
    """
    Update a ticket based on its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE tickets
            SET assign_to = %s, updated_by = %s, updated_at = %s, status = %s, solution = %s
            WHERE ticket_id = %s
        """, (
            ticket.assign_to,
            ticket.updated_by,
            ticket.updated_at,
            ticket.status,
            ticket.solution,
            ticket_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def update_ticket_status(ticket_id, updated_by, updated_at, status):
    """
    Update a ticket status based on its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE tickets
            SET updated_by = %s, updated_at = %s, status = %s
            WHERE ticket_id = %s
        """, (
            updated_by,
            updated_at,
            status,
            ticket_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


def update_ticket_solution(ticket_id, updated_by, updated_at, solution, status='closed'):
    """
    Update a ticket status based on its ID.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            UPDATE tickets
            SET updated_by = %s, updated_at = %s, solution = %s, status = %s
            WHERE ticket_id = %s
        """, (
            updated_by,
            updated_at,
            solution,
            status,
            ticket_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0


class Comment:
    def __init__(self, ticket_id, content, created_by):
        self.ticket_id = ticket_id
        self.content = content
        self.created_by = created_by


def create_comment(comment):
    """
    Create a new comment.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO comments (ticket_id, content, created_by)
            VALUES (%s, %s, %s)
        """, (
            comment.ticket_id,
            comment.content,
            comment.created_by
        ))
        comment_id = cursor.lastrowid
    return comment_id


def get_comments_by_ticket_id(ticket_id):
    """
    Get all comments by ticket id, ordered by created_at descending.
    """
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM comments_management_view
            WHERE ticket_id = %s
            ORDER BY created_at DESC
        """, (ticket_id,))
        comments = cursor.fetchall()
    return comments


def ticket_created_notification(ticket_id):
    ticket = get_ticket_by_id(ticket_id)
    content = f'Ticket "{ticket["subject"]}" has been submitted, please help.'
    url = url_for('ticket.tickets_mgmt', _anchor=f'ticket-{ticket["ticket_id"]}')
    ticket_created.send(ticket, content=content, url=url)


def ticket_assigned_notification(ticket_id):
    ticket = get_ticket_by_id(ticket_id)
    content = f'Ticket "{ticket["subject"]}" has been assigned to you, please process.'
    url = url_for('ticket.tickets_mgmt')
    ticket_assigned.send(ticket, content=content, url=url)


def ticket_processed_notification(ticket_id):
    ticket = get_ticket_by_id(ticket_id)
    content = f'Ticket "{ticket["subject"]}" has been processed, please help.'
    url = url_for('ticket.ticket_by_id', _anchor=f'ticket-{ticket["ticket_id"]}')
    ticket_processed.send(ticket, content=content, url=url)


def ticket_commented_notification(ticket_id):
    ticket = get_ticket_by_id(ticket_id)
    content = f'Ticket "{ticket["subject"]}" has new relpy, please help.'
    url = url_for('ticket.ticket_by_id', ticket_id=ticket_id)
    ticket_commented.send(ticket, content=content, url=url)


def ticket_closed_notification(ticket_id):
    ticket = get_ticket_by_id(ticket_id)
    content = f'Ticket "{ticket["subject"]}" has been closed.'
    url = url_for('ticket.ticket_by_id', ticket_id=ticket_id)
    ticket_closed.send(ticket, content=content, url=url)
