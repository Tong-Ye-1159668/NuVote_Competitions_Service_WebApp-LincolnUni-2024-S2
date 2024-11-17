from flask_wtf import FlaskForm
from wtforms import HiddenField, StringField, TextAreaField, FileField, DateTimeLocalField, SubmitField
from wtforms.validators import DataRequired, ValidationError
from flask_wtf.file import FileAllowed
from datetime import datetime
from datetime import timedelta

from voting.config import ALLOWED_EXTENSIONS


class EventForm(FlaskForm):
    theme_id = HiddenField('Theme ID', validators=[DataRequired()])
    name = StringField('Event Name', validators=[DataRequired()])
    description = TextAreaField('Event Description', validators=[DataRequired()])
    image = FileField('Event Image', validators=[DataRequired(), FileAllowed(ALLOWED_EXTENSIONS, 'Images only!')])
    start_date = DateTimeLocalField('Event Start Date', format='%d/%m/%YT%H:%M', validators=[DataRequired()])
    end_date = DateTimeLocalField('Event End Date', format='%d/%m/%YT%H:%M', validators=[DataRequired()])
    submit = SubmitField('Create')

    def validate_start_date(self, start_date):
        if start_date.data < datetime.now():
            raise ValidationError('Start date cannot be in the past.')

    def validate_end_date(self, end_date):
        if end_date.data <= self.start_date.data:
            raise ValidationError('End date cannot be equal to or earlier than the start date.')

    def __init__(self, *args, **kwargs):
        super(EventForm, self).__init__(*args, **kwargs)
        self.start_date.data = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0) + timedelta(days=1)
        self.end_date.data = self.start_date.data + timedelta(days=30)
