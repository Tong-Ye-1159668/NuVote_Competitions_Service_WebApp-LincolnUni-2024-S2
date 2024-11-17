import logging
from flask import render_template
from voting import app

# bad request 400 error
@app.errorhandler(400)
def bad_request(e):
    '''Render custom 400 error page'''
    return render_template('error/400.html'), 400

# bad request 403 error
@app.errorhandler(403)
def bad_request(e):
    '''Render custom 403 error page'''
    return render_template('error/403.html'), 403

# page not found 404 error
@app.errorhandler(404)
def page_not_found(e):
    '''Render custom 404 error page'''
    return render_template('error/404.html'), 404

# internal server 500 error
@app.errorhandler(500)
def internal_server_error(e):
    '''Render custom 500 error page'''
    message = str(e) or 'An unexpected error occurred.'
    return render_template('error/500.html',message), 500

# for any unhandled exceptions 
@app.errorhandler(Exception)
def handle_exception(e):
    '''Handle all exceptions'''
    # log the exception using the logging module
    logging.exception(e)
    #  render the 500 error page with the exception message.
    message = str(e) or 'An unexpected error occurred.'
    return render_template('error/500.html', message=message), 500
