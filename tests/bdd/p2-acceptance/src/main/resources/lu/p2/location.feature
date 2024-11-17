# Created by hche608 at 20/09/2024
Feature: Location
  I would like to see a location

  Scenario: User add location to profile
    Given I am logged in as a new user
    When I update my location in profile
    Then my location should display on the map

  Scenario: User delete location to profile
    Given I am logged in as a new user
    And I have a valid location in profile
    When I delete my location in profile
    Then my location should not display on the map
    And other users can not see my location

  Scenario: User share the location
    Given I am logged in as a new user
    And I have a valid location
    When I share my location in profile
    Then my location should display on the map
    And other users can see my shared location

  Scenario: User stop sharing the location
    Given I am logged in as a new user
    And I have a valid location
    And I share my location in profile
    When I stop sharing my location in profile
    Then my location should display on the map
    And other users can not see my location

  Scenario: User create an event with location
    Given I am logged in as a new user
    And I have submitted a theme application with location enabled
    And the theme is approved by other admins
    And I create an event with location
    When I add a candidate with location
    And I can publish the event
    Then I should see the candidate on the map view
