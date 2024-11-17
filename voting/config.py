# Define app name

# set application name and slogan, which will be passed to all templates
APP_NAME = 'Wildlife of the Year'
SLOGAN = 'Vote for the Heart of Aotearoa’s Wilderness!'

# Default users information upon registration.
DEFAULT_SECRET_KEY = '16ef49d7e56c50b0de84898da482e8a2'
# IMPORTANT: Change 'ExampleSaltValue' to whatever salt value you'll use in
DEFAULT_PASSWORD_SALT = '16ef49d7e56c50b0de84898da482e8a2'
# Default user description = null, role = voter, status = active
DEFAULT_USER_DESCRIPTION = ''
DEFAULT_USER_ROLE = 'voter'
DEFAULT_USER_STATUS = 'active'

# Defautl path for images
DEFAULT_PROFILE_IMAGES_FOLDER = 'static/uploads/profile_images'
DEFAULT_CANDIDATE_IMAGES_FOLDER = 'static/uploads/competitor_images'
DEFAULT_EVENT_IMAGES_FOLDER = 'static/uploads/competition_images'
DEFAULT_CHARITY_IMAGES_FOLDER = 'static/uploads/charity_images'
DEFAULT_PROFILE_IMAGE = '/static/images/default_profile.png'
DEFAULT_EVENT_IMAGE = '/static/images/default_competition.png'
DEFAULT_CANDIDATE_IMAGE = '/static/images/default_competitor.jpg'
DEFAULT_CHARITY_IMAGE = '/static/images/default_charity.jpg'
# Allowed uploaded image types
ALLOWED_EXTENSIONS = ('png', 'jpg', 'jpeg', 'gif', '.webp', '.bmp')

# Default birthday input
DEFAULT_MIN_BIRTHDATE = '1900-01-01'
DEFAULT_DATE_FORMAT = '%d/%m/%Y'
DEFAULT_DATETIME_FORMAT = '%d/%m/%Y %I:%M:%S %p'

# Validation of inputs
USERNAME_REGX = r'^[a-zA-Z0-9_-]{3,50}$'
DEFAULT_PASSWORD_REGEX = r'^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$'
EMAIL_REGEX = r'^([A-Za-z0-9]+[.-_])*[A-Za-z0-9]+@[A-Za-z0-9-]+(\.[A-Z|a-z]{2,})+$'

# Default vote, event status, announcement status, ticket status
DEFAULT_VOTE_STATUS = 'open'
DEFAULT_VOTE_VALID = True
DEFAULT_EVENT_STATUS = 'draft'
DEFAULT_ANNOUNCEMENT_STATUS = 'active'
DEFAULT_TICKET_STATUS = 'open'
PERMITTED_VOTE_ROLES = ['voter']
MAX_TICKETS_PER_EVENT = 1
# Maximum number of latest voted users to display on the home page
MAX_LATEST_VOTE_USERS = 15
# Maximum number of latest events to display on the home page
MAX_LATEST_EVENTS = 10

# enum of user roles
USER_ROLES = ['voter', 'siteHelper', 'siteAdmin']

