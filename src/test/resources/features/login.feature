Feature: Login
  As a registered trainee
  I want to authenticate with my credentials
  So that I can access protected endpoints with a bearer token

  Background:
    Given a registered trainee exists with first name "Login" and last name "Tester"

  Scenario: Successful login with valid credentials
    When the trainee logs in with their generated credentials
    Then the response status should be 200
    And a JWT token is returned

  Scenario: Login fails with an invalid password
    When the trainee logs in with their username and password "wrong-password"
    Then the response status should be 401

  Scenario: Login fails for a username that does not exist
    When a user logs in with username "no.such.user" and password "whatever-it-is"
    Then the response status should be 401

  Scenario: Accessing a protected endpoint without a token is rejected
    When an unauthenticated request is made to get the trainee's own profile
    Then the response status should be 401

  Scenario: Accessing a protected endpoint with a valid token succeeds
    When the trainee logs in with their generated credentials
    And the trainee requests their own profile using the issued token
    Then the response status should be 200
