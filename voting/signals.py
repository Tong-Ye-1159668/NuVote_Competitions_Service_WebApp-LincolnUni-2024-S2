from blinker import signal

theme_submitted = signal('theme_submitted')
theme_approved = signal('theme_approved')

theme_role_granted = signal('theme_role_granted')
theme_role_changed = signal('theme_role_changed')
theme_role_revoked = signal('theme_role_revoked')

user_banned = signal('user_banned')
user_ban_appealed = signal('user_ban_appealed')
user_ban_appeal_rejected = signal('user_ban_appeal_rejected')
user_ban_appeal_approved = signal('user_ban_appeal_approved')
user_ban_revoked = signal('user_ban_revoked')


ticket_created = signal('ticket_created')
ticket_assigned = signal('ticket_assigned')
ticket_processed = signal('ticket_processed')
ticket_commented = signal('ticket_commented')
ticket_closed = signal('ticket_closed')

event_published = signal('event_published')
# event_started = signal('event_started')
# event_finished = signal('event_finished')
event_verified = signal('event_verified')

user_role_changed = signal('user_role_changed')

charity_submitted = signal('charity_submitted')
charity_approved = signal('charity_approved')
charity_declined = signal('charity_declined')
