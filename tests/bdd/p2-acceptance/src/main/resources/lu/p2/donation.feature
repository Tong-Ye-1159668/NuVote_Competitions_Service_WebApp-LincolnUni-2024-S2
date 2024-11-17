# Created by hche608 at 20/09/2024
Feature: Donation
  I would like to support some charities

  Scenario: Can create a charity
    Given I am logged in as a new user
    And I submit a theme application
    And the theme is approved by other admins
    When I create a charity
    And the charity is created successfully
    And the charity is approved by other admins
    Then I can see donation button on homepage

  Scenario: Can donate
    Given I am logged in as a new user
    When I donate
    Then my donation is succeed
    And I can see my record under dashboard