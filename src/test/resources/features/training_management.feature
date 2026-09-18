Feature: Training management
  As an authenticated trainee
  I want to add and cancel trainings
  So that I can manage my training schedule

  Background:
    Given a registered trainee exists with first name "Train" and last name "Ee"
    And a registered trainer exists with first name "Train" and last name "Er" and specialization id 2
    And the trainee is authenticated

  Scenario: Successfully add a training
    When a training is added for the trainee and trainer on a future date with duration 60
    Then the response status should be 200

  Scenario: Adding a training for a non-existent trainee is rejected
    When a training is added for trainee username "no.such.trainee" and the registered trainer on a future date with duration 60
    Then the response status should be 404

  Scenario: Adding a training for a non-existent trainer is rejected
    When a training is added for the registered trainee and trainer username "no.such.trainer" on a future date with duration 60
    Then the response status should be 404

  Scenario: Adding a training with a non-positive duration is rejected
    When a training is added for the trainee and trainer on a future date with duration -10
    Then the response status should be 400

  Scenario: Cancelling an upcoming training succeeds
    Given a training exists for the trainee and trainer on a future date
    When the most recently created training is cancelled
    Then the response status should be 200

  Scenario: Cancelling a training that has already occurred is rejected
    Given a training exists for the trainee and trainer on a past date
    When the most recently created training is cancelled
    Then the response status should be 409

  Scenario: Cancelling a non-existent training id is rejected
    When training id 999999999 is cancelled
    Then the response status should be 404
