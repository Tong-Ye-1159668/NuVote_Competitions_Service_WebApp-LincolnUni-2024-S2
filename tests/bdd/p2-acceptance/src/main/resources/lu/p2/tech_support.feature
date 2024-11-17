# Created by hche608 at 20/09/2024
Feature: Tech Support
  As a user,
  I want to report an issue,
  so that I can be assisted

  Scenario: Can create a ticket
    Given I am logged in as a new user
    When I create a ticket
    Then My ticket is created successfully
    And I can view the detail of the ticket

  Scenario: Can cancel a ticket
    Given I am logged in as a user
    And I have created a ticket
    When I cancel the ticket
    Then My ticket is cancelled successfully

  Scenario: Can reply to a ticket
    Given I am logged in as a new user
    And I create a ticket
    When I reply to the ticket
    Then I should see all replies about the ticket

  Scenario: Can assign a ticket
    Given I am logged in as a site admin
    And I create a ticket
    And I visit ticket mgmt page
    And the ticket status is new
    When I assign the ticket to a helper
    Then the ticket status is updated to open

  Scenario: Can unassign a ticket
    Given I am logged in as a site admin
    And I create a ticket
    And I visit ticket mgmt page
    And the ticket status is new
    And I assign the ticket to a helper
    When I unassign the ticket to a helper
    Then the ticket status is new


  Scenario Outline: Can close a ticket
    Given I am logged in as a site admin
    And I create a ticket
    And I visit ticket mgmt page
    And I assign the ticket to a helper
    When I close the ticket with "<Resolution>"
    Then the ticket status is updated to closed
    Examples:
      | Resolution |
      | Cancelled  |
      | Drop       |
      | Resolved   |