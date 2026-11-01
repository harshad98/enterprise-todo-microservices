Feature: Authentication API

  Scenario: Register a new user successfully
    Given I have valid registration details
    When I send a POST request to register the user
    Then the registration response status should be 200
    And the response message should be "User registered successfully"