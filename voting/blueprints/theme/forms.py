from flask_wtf import FlaskForm
from wtforms import BooleanField, StringField, TextAreaField, SubmitField
from wtforms.validators import DataRequired

class ThemeForm(FlaskForm):
    theme_name = StringField('Theme Name', validators=[DataRequired()])
    theme_description = TextAreaField('Theme Description', validators=[DataRequired()])
    enable_location = BooleanField('Enable Location', validators=[], render_kw={'value': '1'})
    submit = SubmitField('Submit')

