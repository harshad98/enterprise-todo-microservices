package stepdefinitions;

import api.AuthApi;
import api.TodoApi;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoSteps {

    private final AuthApi authApi = new AuthApi();
    private final TodoApi todoApi = new TodoApi();

    private Response response;

    private String token;
    private String adminToken;

    private String firstUserToken;
    private String secondUserToken;

    private Long todoId;
    private String todoTitle;

    private Map<String, Object> todoRequest;

    // ---------------- USER AUTHENTICATION ----------------

    @Given("I have a valid user authenticated")
    public void createAndAuthenticateUser() {

        token = registerAndLoginUser();
    }

    // ---------------- TODO DETAILS ----------------

    @Given("I have valid Todo details")
    public void prepareTodoDetails() {

        todoTitle = "Test Todo " + UUID.randomUUID();

        todoRequest = new HashMap<>();
        todoRequest.put("title", todoTitle);
        todoRequest.put("description", "Testing Todo API");
        todoRequest.put("completed", false);
    }

    // ---------------- CREATE TODO ----------------

    @When("I send a POST request to create Todo")
    public void createTodo() {

        response = todoApi.createTodo(todoRequest, token);

        todoId = response.jsonPath().getLong("id");
    }

    @Then("the Todo response status code should be {int}")
    public void validateTodoStatusCode(int statusCode) {

        Assert.assertNotNull(response, "Todo API response is null");

        Assert.assertEquals(
                response.getStatusCode(),
                statusCode
        );
    }

    @Then("the response should contain the Todo title")
    public void validateTodoTitle() {

        Assert.assertEquals(
                response.jsonPath().getString("title"),
                todoTitle
        );
    }

    // ---------------- GET MY TODOS ----------------

    @Given("I have created a Todo")
    public void createTodoForTesting() {

        todoTitle = "Test Todo " + UUID.randomUUID();

        Map<String, Object> request = new HashMap<>();
        request.put("title", todoTitle);
        request.put("description", "Testing Todo API");
        request.put("completed", false);

        response = todoApi.createTodo(request, token);

        Assert.assertEquals(response.getStatusCode(), 201);

        todoId = response.jsonPath().getLong("id");
    }

    @When("I send a GET request to fetch my Todos")
    public void getMyTodos() {

        response = todoApi.getMyTodos(token);
    }

    @Then("the response should contain my Todo")
    public void validateMyTodo() {

        Assert.assertTrue(
                response.jsonPath()
                        .getList("title", String.class)
                        .contains(todoTitle)
        );
    }

    // ---------------- GET TODO BY ID ----------------

    @When("I send a GET request to fetch Todo by ID")
    public void getTodoById() {

        response = todoApi.getTodoById(todoId, token);
    }

    @Then("the response should contain the correct Todo ID")
    public void validateTodoId() {

        Assert.assertEquals(
                response.jsonPath().getLong("id"),
                todoId.longValue()
        );
    }

    // ---------------- UPDATE TODO ----------------

    @When("I send a PUT request to update Todo")
    public void updateTodo() {

        String updatedTitle = "Updated Todo " + UUID.randomUUID();

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", updatedTitle);
        updateRequest.put("description", "Updated Todo description");
        updateRequest.put("completed", true);

        todoTitle = updatedTitle;

        response = todoApi.updateTodo(todoId, updateRequest, token);
    }

    @Then("the response should contain the updated Todo title")
    public void validateUpdatedTodo() {

        Assert.assertEquals(
                response.jsonPath().getString("title"),
                todoTitle
        );
    }

    // ---------------- DELETE TODO ----------------

    @When("I send a DELETE request to delete Todo")
    public void deleteTodo() {

        response = todoApi.deleteTodo(todoId, token);
    }

    // ---------------- UNAUTHENTICATED REQUEST ----------------

    @When("I send a GET request to fetch Todos without authentication")
    public void getTodosWithoutAuthentication() {

        response = todoApi.getMyTodosWithoutToken();
    }

    // ---------------- USER DATA ISOLATION ----------------

    @Given("two authenticated users exist")
    public void createTwoAuthenticatedUsers() {

        firstUserToken = registerAndLoginUser();
        secondUserToken = registerAndLoginUser();
    }

    @Given("the first user has created a Todo")
    public void firstUserCreatesTodo() {

        Map<String, Object> request = new HashMap<>();
        request.put("title", "First User Todo " + UUID.randomUUID());
        request.put("description", "Private Todo");
        request.put("completed", false);

        response = todoApi.createTodo(request, firstUserToken);

        Assert.assertEquals(response.getStatusCode(), 201);

        todoId = response.jsonPath().getLong("id");
    }

    @When("the second user tries to access the first user's Todo")
    public void secondUserAccessesFirstUsersTodo() {

        response = todoApi.getTodoById(todoId, secondUserToken);
    }

    // ---------------- ADMIN AUTHENTICATION ----------------

    @Given("I have a valid admin authenticated")
    public void createAndAuthenticateAdmin() {

        adminToken = registerAndLoginAdmin();
    }

    @When("I send a GET request to fetch all Todos as admin")
    public void getAllTodosAsAdmin() {

        response = todoApi.getAllTodosForAdmin(adminToken);
    }

    // ---------------- HELPER METHODS ----------------

    private String registerAndLoginUser() {

        String username = "user_" + UUID.randomUUID();
        String password = "Password@123";

        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("email", username + "@test.com");
        registerRequest.put("password", password);

        Response registerResponse = authApi.register(registerRequest);

        Assert.assertEquals(registerResponse.getStatusCode(), 200);

        return login(username, password);
    }

    private String registerAndLoginAdmin() {

        String username = "admin_" + UUID.randomUUID();
        String password = "Password@123";

        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("email", username + "@test.com");
        registerRequest.put("password", password);

        Response registerResponse = authApi.registerAdmin(registerRequest);

        Assert.assertEquals(registerResponse.getStatusCode(), 200);

        return login(username, password);
    }

    private String login(String username, String password) {

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        Response loginResponse = authApi.login(loginRequest);

        Assert.assertEquals(loginResponse.getStatusCode(), 200);

        return loginResponse.jsonPath().getString("token");
    }
}
