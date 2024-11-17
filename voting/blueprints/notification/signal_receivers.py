from voting.blueprints.theme.models import get_theme_by_id, get_theme_roles_by_theme_id
from voting.blueprints.user.models import get_ban_by_id, get_users_by_role, get_user_by_id
from .models import Notification, create_notification, create_notifications
import voting.signals as signals


def handle_theme_submitted(sender, **kwargs):
    '''Generate notification for all site admins when a theme is submitted'''
    print('Theme submitted', sender, kwargs)
    theme = sender
    admins = get_users_by_role('siteAdmin')
    notifications = [Notification(admin['user_id'], kwargs['content'], kwargs['url']) for admin in admins if admin['user_id'] != theme['created_by']]
    create_notifications(notifications)

def handle_theme_approved(sender, **kwargs):
    print('Theme approved', sender, kwargs)
    theme = sender
    notification = Notification(theme['created_by'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_theme_role_granted(sender, **kwargs):
    print('Theme role granted', sender, kwargs)
    theme_role = sender
    notification = Notification(theme_role['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_theme_role_changed(sender, **kwargs):
    print('Theme role changed', sender, kwargs)
    theme_role = sender
    notification = Notification(theme_role['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_theme_role_revoked(sender, **kwargs):
    print('Theme role revoked', sender, kwargs)
    theme_role = sender
    notification = Notification(theme_role['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_user_banned(sender, **kwargs):
    print('User banned', sender, kwargs)
    banned_user = sender
    notification = Notification(banned_user['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_user_ban_appealed(sender, **kwargs):
    print('User ban appealed', sender, kwargs)
    appeal = sender
    ban = get_ban_by_id(appeal['ban_id'])
    theme_id = ban['theme_id']
    if theme_id != 0:
        theme = get_theme_by_id(theme_id)
        theme_roles = get_theme_roles_by_theme_id(theme_id)
        notifications = [Notification(theme_role['user_id'], kwargs['content'], kwargs['url']) for theme_role in theme_roles if theme_role['user_id'] != ban['user_id']]
    else:
        admins = get_users_by_role('siteAdmin')
        notifications = [Notification(admin['user_id'], kwargs['content'], kwargs['url']) for admin in admins if admin['user_id'] != ban['user_id']]
    create_notifications(notifications)

def handle_user_ban_appeal_rejected(sender, **kwargs):
    print('User ban appeal rejected', sender, kwargs)
    appeal = sender
    banned_user = get_ban_by_id(appeal['ban_id'])
    notification = Notification(banned_user['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_user_ban_appeal_approved(sender, **kwargs):
    print('User ban appeal approved', sender, kwargs)
    user = sender
    notification = Notification(user['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_user_ban_revoked(sender, **kwargs):
    print('User ban revoked', sender, kwargs)
    appeal = sender
    banned_user = get_ban_by_id(appeal['ban_id'])
    notification = Notification(banned_user['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_ticket_created(sender, **kwargs):
    print('Ticket created', sender, kwargs)
    ticket = sender
    admins = get_users_by_role('siteAdmin')
    notifications = [Notification(admin['user_id'], kwargs['content'], kwargs['url']) for admin in admins if admin['user_id'] != ticket['created_by']]
    create_notifications(notifications)

def handle_ticket_assigned(sender, **kwargs):
    print('Ticket assigned', sender, kwargs)
    ticket = sender
    assigner = get_user_by_id(ticket['assign_to'])
    notifications = [Notification(assigner['user_id'], kwargs['content'], kwargs['url'])]
    create_notifications(notifications)

def handle_ticket_processed(sender, **kwargs):
    print('Ticket processed', sender, kwargs)
    ticket = sender
    creator = get_user_by_id(ticket['created_by'])
    notifications = [Notification(creator['user_id'], kwargs['content'], kwargs['url'])]
    create_notifications(notifications)

def handle_ticket_commented(sender, **kwargs):
    print('Ticket commented', sender, kwargs)
    ticket = sender
    creator = get_user_by_id(ticket['created_by'])
    notifications = [Notification(creator['user_id'], kwargs['content'], kwargs['url'])]
    create_notifications(notifications)

def handle_ticket_closed(sender, **kwargs):
    print('Ticket closed', sender, kwargs)
    ticket = sender
    creator = get_user_by_id(ticket['created_by'])
    notifications = [Notification(creator['user_id'], kwargs['content'], kwargs['url'])]
    create_notifications(notifications)

def handle_event_published(sender, **kwargs):
    print('Event published', sender, kwargs)

def handle_event_verified(sender, **kwargs):
    print('Event verified', sender, kwargs)

def handle_user_role_changed(sender, **kwargs):
    print('User role changed')
    user = sender
    notification = Notification(user['user_id'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_charity_submitted(sender, **kwargs):
    '''Generate notification for all site admins when a charity is submitted'''
    print('Charity submitted', sender, kwargs)
    charity = sender
    admins = get_users_by_role('siteAdmin')
    notifications = [Notification(admin['user_id'], kwargs['content'], kwargs['url']) for admin in admins if admin['user_id'] != charity['create_by']]
    create_notifications(notifications)

def handle_charity_approved(sender, **kwargs):
    print('Charity application approved', sender, kwargs)
    charity = sender
    notification = Notification(charity['create_by'], kwargs['content'], kwargs['url'])
    create_notification(notification)

def handle_charity_declined(sender, **kwargs):
    print('Charity application declined', sender, kwargs)
    charity = sender
    notification = Notification(charity['create_by'], kwargs['content'], kwargs['url'])
    create_notification(notification)


signals.theme_submitted.connect(handle_theme_submitted)
signals.theme_approved.connect(handle_theme_approved)

signals.theme_role_granted.connect(handle_theme_role_granted)
signals.theme_role_changed.connect(handle_theme_role_changed)
signals.theme_role_revoked.connect(handle_theme_role_revoked)

signals.user_banned.connect(handle_user_banned)
signals.user_ban_appealed.connect(handle_user_ban_appealed)
signals.user_ban_appeal_rejected.connect(handle_user_ban_appeal_rejected)
signals.user_ban_appeal_approved.connect(handle_user_ban_appeal_approved)
signals.user_ban_revoked.connect(handle_user_ban_revoked)

signals.ticket_created.connect(handle_ticket_created)
signals.ticket_assigned.connect(handle_ticket_assigned)
signals.ticket_processed.connect(handle_ticket_processed)
signals.ticket_commented.connect(handle_ticket_commented)
signals.ticket_closed.connect(handle_ticket_closed)

signals.event_published.connect(handle_event_published)
signals.event_verified.connect(handle_event_verified)

signals.user_role_changed.connect(handle_user_role_changed)

signals.charity_submitted.connect(handle_charity_submitted)
signals.charity_approved.connect(handle_charity_approved)
signals.charity_declined.connect(handle_charity_declined)