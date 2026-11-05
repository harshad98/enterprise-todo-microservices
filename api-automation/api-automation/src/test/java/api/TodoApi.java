package api;

import io.restassured.response.Response;
import specs.RequestSpec;

import static io.restassured.RestAssured.given;

public class TodoApi {
    // Create Todo
    public Response createTodo(Object requestBody, String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)
                .body(requestBody)

                .when()
                .post("/todos");
    }

    // Get all Todos of logged-in user
    public Response getMyTodos(String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)

                .when()
                .get("/todos");
    }

    // Get Todo by ID
    public Response getTodoById(Long id, String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)

                .when()
                .get("/todos/" + id);
    }

    // Update Todo
    public Response updateTodo(Long id, Object requestBody, String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)
                .body(requestBody)

                .when()
                .put("/todos/" + id);
    }

    // Delete Todo
    public Response deleteTodo(Long id, String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)

                .when()
                .delete("/todos/" + id);
    }

    // Admin: Get all Todos
    public Response getAllTodosForAdmin(String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)

                .when()
                .get("/todos/admin/all");
    }

    // Admin: Get any Todo by ID
    public Response getTodoByAdmin(Long id, String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)

                .when()
                .get("/todos/admin/" + id);
    }

    // Admin: Delete any Todo
    public Response deleteTodoByAdmin(Long id, String token) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .header("Authorization", "Bearer " + token)

                .when()
                .delete("/todos/admin/" + id);
    }

    public Response getMyTodosWithoutToken() {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .get("/todos");
    }
}
