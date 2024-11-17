# Created by hche608 at 20/09/2024
Feature: User Ban Management
  As a Theme Scrutineer
  I want to ban users who violate theme rules
  So that I can maintain the integrity of the theme

  Scenario: Ban a user for violating theme rules
    Given I am logged in as a Theme Scrutineer
    And a user has violated the theme rules
    When I choose to ban the user
    Then the user should be banned from the theme
    And the user should receive a notification of the ban
    And the reason for the ban should be recorded
