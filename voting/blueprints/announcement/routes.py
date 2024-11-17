# ENGAGING WITH COMPETITIONS
from flask import render_template, redirect, url_for, request, g, flash

from voting.config import DEFAULT_ANNOUNCEMENT_STATUS
from voting.decorators import admin_required
from .models import Announcement, get_all_announcements, create_announcement, delete_announcement
from voting.utility import are_fields_present
from . import bp


@bp.route('/list')
def announcement_list():
    '''Render the announcements list page'''
    announcements = get_all_announcements()
    announcements.reverse()
    return render_template('announcements/announcement_list.html', announcements=announcements)


@bp.route('/create', methods=['GET', 'POST'])
@admin_required
def announcement_create():
    '''Render the announcement create page'''
    if request.method == 'POST':
        if are_fields_present(request, ['title', 'content', 'end_at']):
            announcement = Announcement(request.form['title'], request.form['content'], request.form['end_at'],
                                        DEFAULT_ANNOUNCEMENT_STATUS, g.user['user_id'])
            create_announcement(announcement)
            flash("Announcement has created successfully", "success")
        return redirect(url_for('announcement.announcements_mgmt'))
    return render_template('announcements/announcement_create.html')


@bp.route('/edit/<int:announcement_id>')
@admin_required
def announcement_edit(announcement_id):
    '''Render the announcement edit page'''
    return render_template('announcements/announcement_edit.html')


@bp.route('/delete/<int:announcement_id>', methods=['POST'])
@admin_required
def announcement_delete(announcement_id):
    '''Delete the announcement'''
    delete_announcement(announcement_id)
    return redirect(url_for('announcement.announcements_mgmt'))


@bp.route('/view/<int:announcement_id>')
def announcement_view(announcement_id):
    return render_template('announcements/announcement_view.html')

# Render the admin home page with user information
@bp.route('/')
@admin_required
def announcements_mgmt():
    '''Render the announcements management page'''
    announcements = get_all_announcements()
    announcements.reverse()
    return render_template('announcements/announcements_mgmt.html', announcements=announcements)
