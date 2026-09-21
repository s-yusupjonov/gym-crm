Feature: Trainer registration
  As a prospective gym trainer
  I want to register with a specialization
  So that I can be issued credentials and be assigned trainees

  Scenario: Successful trainer registration
    When a trainer registers with first name "Carl" and last name "Coach" and specialization id 1
    Then the response status should be 201
    And the registration response should contain a generated username and password

  Scenario: Trainer registration is rejected for a non-existent specialization id
    When a trainer registers with first name "Carl" and last name "Coach" and specialization id 999999
    Then the response status should be 400

  Scenario: Trainer registration is rejected when the first name is blank
    When a trainer registers with first name "" and last name "Coach" and specialization id 1
    Then the response status should be 400
    And the response should contain a field error for "firstName"
