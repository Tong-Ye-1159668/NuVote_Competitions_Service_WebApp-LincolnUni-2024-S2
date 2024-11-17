# NuVote Competition as a Service Platform for Conservation Groups
#### Submitted on 03 November 2024
This repository contains a group project developed for the COMP 639 Studio Project during Semester 2 of my Master of Applied Computing studies at Lincoln University. Building upon our "NZ ___ of the Year" platform, this web application serves as a competition management service enabling conservation groups to create and manage their own "___ of the Year" competitions to promote environmental awareness and conservation efforts in New Zealand.

## Try demo here!
https://comp639nup2.pythonanywhere.com/

## Interface
Main Page

![image](https://github.com/user-attachments/assets/a4bd46c9-d743-461b-acc2-4bbfa25a4cb0)

Voting Page

![image](https://github.com/user-attachments/assets/4302280b-9d72-49e9-bde6-1121c999151e)

Voting Result Page

![image](https://github.com/user-attachments/assets/893d5ec8-5c4e-42eb-a4e6-909bec73c8d1)

Voting Management Page

![image](https://github.com/user-attachments/assets/74391f8f-f3e2-41c3-9ea0-0a91c0f756f3)

User Profile with Location Page

![image](https://github.com/user-attachments/assets/1c99f12f-e434-4956-995c-5e99f3688bfd)

User Donation Success Page

![image](https://github.com/user-attachments/assets/091f6294-f39b-41c1-b820-49609861dbf0)

User Management Page

![image](https://github.com/user-attachments/assets/d3299396-02e8-4dfc-ad33-b435f7dd75d2)

Tech Support Page

![image](https://github.com/user-attachments/assets/42ac9ae0-cf0c-4887-b50a-fc5f6beb6278)


## Features
- **Multi-Competition Platform**:
  - Enables conservation groups to create and manage their own competitions.
  - Competition approval process managed by Site Admins.
  - Customizable competition settings for each event.
  - Supports multiple concurrent "___ of the Year" competitions.

- **Role Management**:
  - **Site Admin**: Overall platform administration and competition approval.
  - **Site Helper**: Technical support and user assistance.
  - **Theme Admin**: Competition-specific management and settings.
  - **Theme Scrutineer**: Competition-specific voting integrity monitoring.
  - **Voter**: Participation across multiple competitions.

- **Location Features**:
  - Interactive maps showing competitor locations.
  - Distance calculation from user's location to competitors.
  - Geographic visualization of voting patterns.
  - Integration with OpenStreetMap and Leaflet.
  - Privacy-focused user location display.

- **Tech Support System**:
  - Integrated help desk functionality.
  - User support request tracking.
  - Bug reporting and resolution system.
  - Support queue management for Site Helpers.
  - Request status tracking and updates.

- **Advanced Scrutineering**:
  - Theme-specific ban management.
  - Ban appeal system for users.
  - Voting integrity dashboard.
  - Transparent ban review process.
  - Multi-level ban system (competition-specific and site-wide).

- **Donation System**:
  - Secure donation processing for registered charities.
  - Tax receipt generation and management.
  - Charity verification process.
  - Donation tracking and reporting.
  - Historical donation record access.

- **Theme Management**:
  - Create and manage competition/theme events.
  - Competitor profile management.
  - Voting period control.
  - Results tracking and display.
  - Competition analytics and reporting.

- **Security Features**:
  - Role-based access control.
  - Voting integrity protection.
  - Secure donation handling.
  - Data privacy safeguards.

## Technical Details
- **Technologies Used**:
  - Backend: Python (Flask)
  - Database: MySQL
  - Frontend: Bootstrap CSS, JavaScript
  - Mapping: Leaflet with OpenStreetMap

- **Architecture**:
  - Multi-tenant database design
  - Role-based access control
  - Scalable competition management

- **Security Features**:
  - Advanced voting fraud prevention
  - Secure user authentication
  - Data privacy protection

## Purpose
This project emphasizes my ability to:
- Work effectively as a team using Agile Scrum methodology with Jira for task management.
- Collaborate efficiently using Git branch strategy for version control and code integration.
- Design and implement complex web applications through collaborative development.
- Manage and coordinate multiple development tasks across team members.

The platform stands as a testament to our collaborative effort in delivering a comprehensive solution for conservation groups while demonstrating effective team communication and project management skills.

---

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- Python 3.x
- pip (Python package installer)
- MySQL or compatible database

## Installation
1. Setup timezone
```shell
export TZ="Pacific/Auckland"

# Modify wsgi.py
import os
import time

os.environ["TZ"] = "Pacific/Auckland"
time.tzset()
```


1. Clone the repository:

    ```sh
    git clone https://github.com/Tong-Ye-1159668/NuVote_Competitions_Service_WebApp-LincolnUni-2024-S2.git
    cd NuVote_Competitions_Service_WebApp-LincolnUni-2024-S2
    ```

2. Create a virtual environment and activate it:

    ```sh
    python -m venv venv
    source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
    ```

3. Install the required Python packages:

    ```sh
    pip install -r requirements.txt
    ```

4. Configure the database connection. Create a `connect.py` file in the project root with your database settings:

    ```python
    DB_HOST = 'localhost'
    DB_USER = 'your_db_user'
    DB_PASSWORD = 'your_db_password'
    DB_NAME = 'your_db_name'
    SECRET_KEY = 'your_secret_key'
    ```

5. Initialize the database with the required tables. You can find the SQL scripts in the `sql/` directory. Run the scripts using your preferred method (e.g., MySQL Workbench, command line).
```shell
# Create tables
source /data/schema.sql;

# Populate data
source /data/population.sql;

# reset db
source /data/reset_db.sql;

```
## Running the Application

1. Ensure your MySQL database server is running and accessible.

2. Start the Flask application:

    ```sh
    FLASK_APP=run.py flask run
    ```

3. Open your web browser and navigate to `http://localhost:5000`.

## Usage

### User Registration

- Navigate to the registration page (`http://localhost:5000/register`).
- Fill in the required fields and submit the form to create a new account.

### User Login

- Navigate to the login page (`http://localhost:5000/login`).
- Enter your username and password to log in.

### Profile Management

- After logging in, navigate to your profile page (`http://localhost:5000/profile/<your_user_id>`).
- Update your profile information and save changes.

### Admin Functions

- Admin users can access the admin home page (`http://localhost:5000/users`) to manage user accounts.

## Project Structure

- `app/`: Contains the Flask application modules and templates
- `static/`: Static files (e.g., CSS, JavaScript, images)
- `templates/`: HTML templates for rendering pages
- `yob/`: Application logic and utility functions
- `tests`: Test functions
- `config.py`: Configuration settings for the application
- `connect.py`: Database settings for the application
- `requirements.txt`: Python dependencies

## Testing

This project uses [Cypress](https://www.cypress.io/) for end-to-end (E2E) testing, providing a robust and reliable solution for testing. Cypress stands out from other testing frameworks with its ability to perform tests directly in the browser, offering real-time reloading, automatic waiting, and an interactive GUI, making it easier to debug and develop tests.

### Setup

1. **Install Node.js**  
   Ensure you have [Node.js](https://nodejs.org/) installed, as Cypress requires it to run.

2. **Install Dependencies**  
   Navigate to the `tests/e2e` directory and run the following command to install all required dependencies:
```bash
   npm install
```
### Running Tests
You can run Cypress tests in two different modes:

#### Interactive Mode
Open Cypress in interactive mode to visually see the tests running in the browser:
```bash
npm run open
```
#### Headless Mode
Run all tests in headless mode without opening the GUI, useful for CI/CD pipelines:
```bash
npm run test
```
This command will execute all the E2E tests in the terminal and generate a test report.

### Additional Information
- Test Files: All test cases are located in the `tests/e2e/cypress/e2e` directory. The test files are written in JavaScript.
- Configuration: Cypress configuration settings are managed in the `cypress.config.js` file.
- Test Reports: Test results, including screenshots and videos (in headless mode), are stored in the `tests/e2e/cypress/reports directory.

## Contributing

Contributions are welcome! Please submit a pull request
