Feature: User Registration and Login
  As a user, I want to register on the website.

  Scenario: Successful user registration
    Given I am on the registration page
    When I provide my details to register
    Then I should be redirected to my account as a logged-in user
    And I should see a message confirming successful registration

  Scenario: Successful user login
    Given I am on the login page
    And I have a valid username and password
    When I log in
    Then I should be redirected to my account as a logged-in user

  Scenario: Successful user logout
    Given I am on the login page
    And I have a valid username and password
    And I log in
    When I log out
    Then I am logged out successfully

  Scenario: Error feedback for invalid registration
    Given I am on the registration page
    When I submit invalid information
    Then I should see an error message indicating the invalid data on the registration page

  Scenario: Error feedback for invalid login
    Given I am on the login page
    When I submit invalid credentials
    Then I should see an error message indicating the invalid data on the login page