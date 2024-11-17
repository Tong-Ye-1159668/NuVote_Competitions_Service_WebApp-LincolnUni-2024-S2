#  import the app object from a module named voting
from voting import app

if __name__ == "__main__":
    '''Enables debug mode and sets the server to run on port 80, which is the default HTTP port'''
    app.run(debug=True, port=80)
