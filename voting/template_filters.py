from datetime import timedelta
from zoneinfo import ZoneInfo

from voting import app
from voting.config import DEFAULT_DATE_FORMAT, DEFAULT_DATETIME_FORMAT


@app.template_filter('format_relative_time')
def format_relative_time(time_diff):
    """Custom Jinja filter, format timedelta to human-readable style"""
    if isinstance(time_diff, timedelta):
        intervals = [
            ('year', timedelta(days=365)),
            ('month', timedelta(days=30)),
            ('week', timedelta(weeks=1)),
            ('day', timedelta(days=1)),
            ('hour', timedelta(hours=1)),
            ('minute', timedelta(minutes=1)),
            ('second', timedelta(seconds=1))
        ]

        for label, interval in intervals:
            count = int(time_diff / interval)
            if count >= 1:
                # Return a formatted string with the appropriate time label
                return f"{count} {label}{'s' if count > 1 else ''}"

        return 'now'
    else:
        raise TypeError("Expected timedelta type for time_diff")


@app.template_filter('datetime_format')
def datetime_format(value):
    if value is None or value == '':
        return ''
    return value.strftime(DEFAULT_DATETIME_FORMAT)

@app.template_filter('utc_2_nzst_datetime_format')
def utc_2_nzst_datetime_format(value):
    if value is None or value == '':
        return ''
    # Add UTC timezone
    added_tz = value.replace(tzinfo=ZoneInfo('UTC'))
    # Convert to New Zealand time (NZST)
    nz_dt = added_tz.astimezone(ZoneInfo('Pacific/Auckland'))
    return nz_dt.strftime(DEFAULT_DATETIME_FORMAT)


@app.template_filter('date_format')
def date_format(value):
    if value is None or value == '':
        return ''
    return value.strftime(DEFAULT_DATE_FORMAT)
