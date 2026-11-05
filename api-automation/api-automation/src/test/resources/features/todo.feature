
Feature: Todo Microservice API Testing

  # CREATE TODO

  Scenario: Create a new Todo successfully
    Given I have a valid user authenticated
    And I have valid Todo details
    When I send a POST request to create Todo
    Then the Todo response status code should be 201
    And the response should contain the Todo title


  # GET TODOS

  Scenario: Get all Todos of logged-in user
    Given I have a valid user authenticated
    And I have created a Todo
    When I send a GET request to fetch my Todos
    Then the Todo response status code should be 200
    And the response should contain my Todo


  # GET TODO BY ID

  Scenario: Get a Todo by ID
    Given I have a valid user authenticated
    And I have created a Todo
    When I send a GET request to fetch Todo by ID
    Then the Todo response status code should be 200
    And the response should contain the correct Todo ID


  # UPDATE TODO

  Scenario: Update an existing Todo
    Given I have a valid user authenticated
    And I have created a Todo
    When I send a PUT request to update Todo
    Then the Todo response status code should be 200
    And the response should contain the updated Todo title


  # DELETE TODO

  Scenario: Delete an existing Todo
    Given I have a valid user authenticated
    And I have created a Todo
    When I send a DELETE request to delete Todo
    Then the Todo response status code should be 204


  # SECURITY TESTING

  Scenario: Access Todos without JWT token
    When I send a GET request to fetch Todos without authentication
    Then the Todo response status code should be 401


  # AUTHORIZATION TESTING

  Scenario: User cannot access another user's Todo
    Given two authenticated users exist
    And the first user has created a Todo
    When the second user tries to access the first user's Todo
    Then the Todo response status code should be 404


  # ADMIN TESTING

  Scenario: Admin can fetch all Todos
    Given I have a valid admin authenticated
    When I send a GET request to fetch all Todos as admin
    Then the Todo response status code should be 200
