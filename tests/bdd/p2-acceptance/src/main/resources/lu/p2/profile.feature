# Created by hche608 at 18/09/2024
Feature: Profile Management
  As a user, I want to manage my profile effectively.

  Scenario: Update my own profile
    Given I am logged in as a new user
    And I navigate to the profile page
    When I update my profile details
    Then my profile details should be successfully updated

  Scenario: Remove my own profile
    Given I am logged in as a new user
    And I navigate to the profile page
    When I delete my profile details
    Then my profile should be removed

  Scenario: View my own profile
    Given I am logged in as a new user
    And I navigate to the profile page
    When I view my profile details
    Then I should see my current profile information

  Scenario: Can update my profile image
    Given I am logged in as a new user
    And I navigate to the profile page
    When I update my profile image
    Then my profile image is updated

  Scenario: Can delete my profile image
    Given I am logged in as a new user
    And I navigate to the profile page
    And I have a profile image
    When I remove my profile image
    Then my profile image is removed