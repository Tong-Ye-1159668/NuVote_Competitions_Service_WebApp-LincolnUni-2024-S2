# Created by hche608 at 20/09/2024
Feature: Role Based Access Control
  As a Site Admin,
  I want to promote or demote users to/from the Site Admin role,
  so that I can manage platform-wide roles effectively.

  Scenario Outline: Site Admin Promote a User to "<Role>"
    Given I am logged in as a site admin
    When I assign a site "<Role>" to the voter user
    Then the user is promoted to the site "<Role>"
    Examples:
      | Role       |
      | siteHelper |
      | siteAdmin  |

  Scenario Outline: Site Admin Demote a User to "<Role>"
    Given I am logged in as a site admin
    And there is a power user with "<PowerUserRole>"
    When I assign a site "<Role>" to the power user
    Then the user is demoted to the site "<Role>"
    Examples:
      | PowerUserRole | Role       |
      | Helper        | voter      |
      | Admin         | siteHelper |

  Scenario: Guest Can view events details
    Given I am a guest
    When I visit the event details
    Then I see event details
    And I see candidates details