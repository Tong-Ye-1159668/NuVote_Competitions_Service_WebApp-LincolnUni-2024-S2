import os
import pkgutil

def register_blueprints(app):
    # Get the path of the current directory (where __init__.py is located)
    current_dir = os.path.dirname(__file__)
    
    # Iterate over all modules in the current directory and import them
    for _, module_name, _ in pkgutil.iter_modules([current_dir]):
        if module_name != '__init__':
            # Dynamically import the module
            module = __import__(f'{__package__}.{module_name}', fromlist=[module_name])
            
            # Check if the module has a 'bp' or 'blueprint' object (this is the Blueprint)
            if hasattr(module, 'bp'):
                app.register_blueprint(module.bp)
                print(f'Registered blueprint: {module_name}')
            else:
                print(f'No bp found in module {module_name}')
