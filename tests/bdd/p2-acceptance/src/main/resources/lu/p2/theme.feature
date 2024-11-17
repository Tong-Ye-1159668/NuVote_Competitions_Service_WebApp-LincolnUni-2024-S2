# Created by hche608 at 18/09/2024
Feature: Create or Manage a Theme
  As a User,
  I want to create a new theme
  So that I can promote a specific conservation cause.

  Scenario: User Submits a Theme Application
    Given I am logged in as a new user
    When I submit a theme application
    Then the theme application is created
    And I should see the new theme application under my themes

  Scenario: User Delete a Theme Application
    Given I am logged in as a new user
    And I submit a theme application
    And the theme application has not been approved
    When I delete my theme application
    Then the theme application is deleted

  Scenario: User Becomes First Theme Admin After Approval
    Given I am logged in as a new user
    And I submit a theme application
    When the theme is approved by other admins
    Then I am assigned as the theme admin for the theme

  Scenario Outline: Site Admin "<action>" a Theme Application
    Given there is a pending applications
    And I am logged in as a site admin
    When I "<action>" an application
    Then the application is marked as "<status>"
    And applicant should see the "<status>" theme
    Examples:
      | action  | status   |
      | Approve | Accepted |
      | Reject  | Rejected |

  Scenario Outline: Theme Admin Can Assign Theme Roles to a User
    Given I am logged in as a new user
    And I submit a theme application
    And the theme is approved by other admins
    When I assign a theme "<Role>" to a standard user
    Then the user is promoted to the "<Role>"
    Examples:
      | Role        |
      | tScrutineer |
      | tAdmin      |