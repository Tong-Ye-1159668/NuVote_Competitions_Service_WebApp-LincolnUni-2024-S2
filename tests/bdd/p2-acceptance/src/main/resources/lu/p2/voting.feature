# Created by hche608 at 20/09/2024
Feature: Voting
  As a user,
  I want to cast my vote,
  so that I can support my ultimate conservation efforts.

  Scenario: User successfully casts a vote
    Given I am logged in as a new user
    And there is an active event available for voting
    When I vote the event
    Then my vote should be recorded
    And I should not able to vote the same event again
    And I can see my vote under my dashboard

  Scenario: User attempts to vote without being logged in
    Given I am a guest
    And there is an active event available for voting
    When I try to cast a vote
    Then I should see the login page
