import hashlib
import locale
import random
import string
from datetime import datetime

from flask import request

from voting.config import ALLOWED_EXTENSIONS


def get_current_datetime():
    """Return the current date and time."""
    return datetime.now()


def random_string(length=32):
    """Generate a random string of specified length."""
    # Define the character set (uppercase, lowercase letters, and digits)
    characters = string.ascii_letters + string.digits

    # Generate a random string using the character se
    return ''.join(random.choice(characters) for _ in range(length))


def are_fields_present(request, required_fields):
    """Check if all required fields are present in the request."""
    return all(field in request.form and len(request.form[field]) >= 0 for field in required_fields)


def get_locale_from_request(request):
    """
    Determine the best matching locale based on the Accept-Language header.
    """
    supported_locales = [
        'en_NZ.UTF-8', 'fr_FR.UTF-8', 'de_DE.UTF-8',
        'es_ES.UTF-8', 'zh_CN.UTF-8', 'zh_TW.UTF-8'
    ]

    # Extract the language code from the Accept-Language header
    best_match = request.accept_languages.best_match(['en', 'fr', 'de', 'es', 'zh'])

    # Map the best match to a locale name
    locale_map = {
        'en': 'en_NZ.UTF-8',  # New Zealand English
        'fr': 'fr_FR.UTF-8',  # French
        'de': 'de_DE.UTF-8',  # German
        'es': 'es_ES.UTF-8',  # Spanish
        'zh': 'zh_CN.UTF-8',  # Simplified Chinese
        'zh-Hant': 'zh_TW.UTF-8',  # Traditional Chinese
    }

    return locale_map.get(best_match, 'en_NZ.UTF-8')


def format_by_locate(value, format='%x'):
    """
    Format a datetime object as a string using a locale-specific date format.
    The locale is derived from the request's Accept-Language header.
    """
    if value is None:
        return ""

    # Get the best matching locale from the Accept-Language header
    user_locale = get_locale_from_request(request)

    try:
        # Set the locale for date formatting
        locale.setlocale(locale.LC_TIME, user_locale)
    except locale.Error:
        # Fallback to a default locale if the user's locale is not available
        locale.setlocale(locale.LC_TIME, 'C')

    # Use the locale's date time format
    short_date_format = format  # %c is the locale's preferred date and time representation
    return value.strftime(short_date_format)


def allowed_file(filename):
    """Validate profile image extension"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def read_file_extension(filename):
    '''Read file extension'''
    return filename.rsplit('.', 2)[1].lower()


def calculate_md5_hash(file):
    """Calculate the MD5 hash of a file-like object."""
    hasher = hashlib.md5()
    while chunk := file.read(8192):
        hasher.update(chunk)
    # Reset the file pointer to the beginning after reading
    file.seek(0)
    return hasher.hexdigest()


def get_hashed_filename(file, extension):
    '''Generate hashed filename'''
    return calculate_md5_hash(file) + '.' + extension

def get_real_ip(request):
    """Get real ip, especially when server behiend a proxy"""
    # If X-Forwarded-For is not available, check the X-Real-IP header
    if request.headers.get('X-Real-IP'):
        ip = request.headers.get('X-Real-IP')
    # First, try to get the real IP address from the X-Forwarded-For header
    elif request.headers.getlist("X-Forwarded-For"):
        ip = request.headers.getlist("X-Forwarded-For")[0]
    # As a last resort, use the remote address directly from the request
    else:
        ip = request.remote_addr
    return ip