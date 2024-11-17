from flask import g, redirect, render_template, url_for

from voting.decorators import json_response, login_required
from voting.response import Response
from . import models
from . import bp

@bp.route('/my')
@login_required
def my_notifications():
    return render_template('notifications/my_notifications.html')


@bp.route('/unread')
@login_required
@json_response
def get_unread_notifications():
    notifications = models.get_unread_notifications(g.user['user_id'])
    return Response.success("Unread notifications", notifications)

@bp.route('/all')
@login_required
@json_response
def get_all_notifications():
    notifications = models.get_all_notifications(g.user['user_id'])
    return Response.success("All notifications", notifications)

@bp.route('/read/<int:notification_id>', methods=['POST'])
@login_required
@json_response
def mark_as_read(notification_id):
    notification = models.get_notification_by_id(notification_id)
    if notification is None:
        return Response.error('Notification not found')
    if notification['user_id'] != g.user['user_id']:
        return Response.error('You are not authorized to read this notification')
    models.mark_notification_as_read(notification_id)
    return Response.success()

@bp.route('/allread', methods=['POST'])
@login_required
@json_response
def mark_all_as_read():
    count = models.mark_all_notifications_as_read(g.user['user_id'])
    return Response.success("Marked all as read", count)
