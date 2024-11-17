import mysql.connector
from flask import current_app as app
from mysql.connector import Error

from voting import connect # User credentials and database details are imported from a 'connect' module

# Create a database connection pool
db_pool = mysql.connector.pooling.MySQLConnectionPool(
    pool_name="mypool",
    pool_size=5,  # 5 connections
    pool_reset_session=True,
    user=connect.dbuser, # import user from 'connect' module
    password=connect.dbpass, # import password from 'connect' module
    host=connect.dbhost, # import host from 'connect' module
    database=connect.dbname, # import database name from 'connect' module
    auth_plugin='mysql_native_password',
    autocommit=True  # Autocommit is set to True
)


def get_connection():
    """Get a connection from the pool and enable autocommit"""
    try:
        connection = db_pool.get_connection()
        if connection.is_connected():
            # connection.autocommit = True  # Enable autocommit
            return connection
        else:
            raise Error("Failed to connect to the database.") # error handling
    except Error as e:
        app.logger.error(f"Error getting connection from pool: {e}") # logging error
        raise

# a Cursor class that acts as a context manager for database operations
class Cursor:
    """Context Manager of Cursor"""

    def __init__(self, **cursor_params):
        # Initializes connection and cursor as None.
        self.connection = None
        self.cursor = None
        # Set default cursor parameters with dictionary=True
        self.cursor_params = {'dictionary': True}
        # Update with any additional provided parameters
        self.cursor_params.update(cursor_params)

    def __enter__(self):
        try:
            #  get a database connection
            self.connection = get_connection()
            # if connected
            if self.connection.is_connected():
                # create a cursor with the specified parameters.
                self.cursor = self.connection.cursor(**self.cursor_params)
            # if not connected
            else:
                raise Error("Connection is not available.")
        except Error as e:
            app.logger.error(f"Error in Cursor __enter__: {e}")
            raise
        # return 
        return self.cursor

    def __exit__(self, exc_type, exc_val, exc_tb):
        # close cursor
        if self.cursor:
            try:
                self.cursor.close() # cleanup operations
            except Error as e:
                app.logger.error(f"Error closing cursor: {e}")
            # ensure cleanup cursor
            self.cursor = None
        # close connection
        if self.connection:
            try:
                self.connection.close()  # Return the connection to the pool
            except Error as e:
                app.logger.error(f"Error closing connection: {e}")
            # ensure cleanup connection
            self.connection = None
