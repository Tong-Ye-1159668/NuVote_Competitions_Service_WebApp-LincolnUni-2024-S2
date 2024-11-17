# Created by hche608 at 20/09/2024
Feature: Create or Manage an Event
  As a Theme Admin
  I want to create and manage events within an existing theme
  So that I can promote events effectively over time

  Scenario: Theme Admin Can Create an Event
    Given I am logged in as a new user
    And I submit a theme application
    And the theme is approved by other admins
    When I create an event
    Then the event is created
    And I can add candidates to the event
    And I can publish the event

  Scenario: Theme Admin Can Delete an Event
    Given I am logged in as a new user
    And I submit a theme application
    And the theme is approved by other admins
    And I create an event
    And the event is created
    When I delete the event
    Then the event is deleted

  Scenario: Theme Admin manages an existing draft event
    Given I am logged in as a new user
    And I submit a theme application
    And the theme is approved by other admins
    And I create an event
    And the event is created
    When I update the event details
    Then the event details is successfully updated
    And I can view the updated event information
