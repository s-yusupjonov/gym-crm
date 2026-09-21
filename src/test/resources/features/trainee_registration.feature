Feature: Trainee registration
  As a prospective gym member
  I want to register as a trainee
  So that I can be issued credentials and start booking trainings

  Scenario: Successful trainee registration
    When a trainee registers with first name "Alice" and last name "Anderson"
    Then the response status should be 201
    And the registration response should contain a generated username and password

  Scenario: Trainee registration is rejected when the first name is blank
    When a trainee registers with first name "" and last name "Anderson"
    Then the response status should be 400
    And the response should contain a field error for "firstName"

  Scenario: Trainee registration is rejected when the last name is blank
    When a trainee registers with first name "Alice" and last name ""
    Then the response status should be 400
    And the response should contain a field error for "lastName"
