
Feature: Auth Microservice API Testing

  Scenario: Register a new user successfully
    Given I have valid user registration details
    When I send a POST request to register API
    Then the response status code should be 200
    And the response should contain "User registered successfully"

  Scenario: Login with valid credentials
      Given I have a registered user
      When I send a POST request to login API
      Then the response status code should be 200
      And the response should contain a valid JWT token

    Scenario: Login with incorrect password
      Given I have a registered user
      When I send a login request with incorrect password
      Then the login request should fail

    Scenario: Login with incorrect username
      Given I have a registered user
      When I send a login request with incorrect username
      Then the login request should fail


Scenario: Register a new admin successfully
    Given I have valid admin registration details
    When I send a POST request to register admin API
    Then the response status code should be 200
    And the response should contain "Admin registered successfully"

Scenario: Login as admin and validate admin role
    Given I have valid admin registration details
    When I send a POST request to register admin API
    Then the response status code should be 200
    When I send a POST request to admin login API
    Then the response status code should be 200
    And the admin JWT should contain ROLE_ADMIN

Scenario: Register admin with duplicate username and email
    Given I have valid admin registration details
    When I send a POST request to register admin API
    Then the response status code should be 200
    When I resend the same admin registration request
    Then the response status code should be 409