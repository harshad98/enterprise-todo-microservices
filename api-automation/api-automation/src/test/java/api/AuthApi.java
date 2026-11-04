package api;
import io.restassured.response.Response;
import specs.RequestSpec;
import static io.restassured.RestAssured.given;
//Its responsibility is to send HTTP requests to the Auth Microservice.
public class AuthApi {
    public Response register(Object requestBody) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .body(requestBody)

                .when()
                .post("/auth/register");
    }

    public Response login(Object requestBody) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .body(requestBody)

                .when()
                .post("/auth/login");
    }

    public Response registerAdmin(Object requestBody) {

        return given()
                .spec(RequestSpec.getRequestSpec())
                .body(requestBody)

                .when()
                .post("/auth/register-admin");
    }
}
