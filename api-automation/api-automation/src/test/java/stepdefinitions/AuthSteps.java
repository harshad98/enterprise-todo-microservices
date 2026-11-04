
package stepdefinitions;

import api.AuthApi;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;
import specs.RequestSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class AuthSteps {

    private AuthApi authApi = new AuthApi();
    private Response response;
    private Map<String, String> requestBody;
    private String username;
    private String password;

    private String token;

    private JsonNode jwtPayload;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("I have valid user registration details")
    public void validUserRegistrationDetails() {

        String uniqueId = UUID.randomUUID().toString();

        requestBody = new HashMap<>();

        requestBody.put("username", "testuser_" + uniqueId);
        requestBody.put("email", "test_" + uniqueId + "@gmail.com");
        requestBody.put("password", "Test@12345");
    }

    @When("I send a POST request to register API")
    public void sendRegisterRequest() {

        response = authApi.register(requestBody);
    }

    @Then("the response status code should be {int}")
    public void validateStatusCode(int statusCode) {

        assertEquals(response.getStatusCode(), statusCode);
    }

    @Then("the response should contain {string}")
    public void validateResponseBody(String expectedMessage) {

        assertTrue(
                response.getBody().asString()
                        .contains(expectedMessage)
        );
    }
//LOGIN-----------------------------------------------------------------------------------
    // Create a user before testing login
    @Given("I have a registered user")
    public void createRegisteredUser() {

        String uniqueId = UUID.randomUUID().toString();

        username = "loginuser_" + uniqueId;
        password = "Test@12345";

        Map<String, String> registerBody = new HashMap<>();

        registerBody.put("username", username);
        registerBody.put("email", "login_" + uniqueId + "@gmail.com");
        registerBody.put("password", password);

        Response registerResponse = authApi.register(registerBody);

        assertEquals(registerResponse.getStatusCode(), 200);
    }

    // Send login request with valid credentials
    @When("I send a POST request to login API")
    public void sendLoginRequest() {

        Map<String, String> loginBody = new HashMap<>();

        loginBody.put("username", username);
        loginBody.put("password", password);

        response = authApi.login(loginBody);
    }

    // Validate JWT token
    @Then("the response should contain a valid JWT token")
    public void validateJwtToken() throws Exception {

        // Extract JWT token from response
        token = response.jsonPath().getString("token");

        // Validate token is not null or empty
        assertTrue(token != null && !token.isBlank(),
                "JWT token should not be empty");

        // JWT should contain three parts
        String[] parts = token.split("\\.");

        assertEquals(parts.length, 3,
                "JWT should contain Header, Payload and Signature");

        // Decode JWT payload
        String decodedPayload = new String(
                Base64.getUrlDecoder().decode(parts[1])
        );

        // Convert JSON payload into JsonNode
        jwtPayload = objectMapper.readTree(decodedPayload);

        // Validate subject
        assertTrue(jwtPayload.hasNonNull("sub"),
                "JWT should contain user ID");

        // Validate roles
        assertTrue(jwtPayload.has("roles"),
                "JWT should contain roles");

        assertTrue(
                jwtPayload.get("roles").toString().contains("ROLE_USER"),
                "JWT should contain ROLE_USER"
        );

        // Validate issued time
        assertTrue(jwtPayload.has("iat"),
                "JWT should contain issued time");

        // Validate expiration time
        assertTrue(jwtPayload.has("exp"),
                "JWT should contain expiration time");

        System.out.println("JWT Token: " + token);
        System.out.println("JWT Payload: " + decodedPayload);
    }

    // Login with incorrect password
    @When("I send a login request with incorrect password")
    public void loginWithIncorrectPassword() {

        Map<String, String> loginBody = new HashMap<>();

        loginBody.put("username", username);
        loginBody.put("password", "WrongPassword@123");

        response = authApi.login(loginBody);
    }

    // Login with incorrect username
    @When("I send a login request with incorrect username")
    public void loginWithIncorrectUsername() {

        Map<String, String> loginBody = new HashMap<>();

        loginBody.put("username", "invalid_" + UUID.randomUUID());
        loginBody.put("password", password);

        response = authApi.login(loginBody);
    }

    // Validate unsuccessful authentication
    @Then("the login request should fail")
    public void validateLoginFailure() {

        assertTrue(response.getStatusCode() >= 400,
                "Login should fail for invalid credentials");

        System.out.println("Login failure response: "
                + response.getBody().asString());
    }

    //ADMIN APIs---------------------------------------------------------------------------

    @Given("I have valid admin registration details")
    public void validAdminRegistrationDetails() {

        String uniqueId = UUID.randomUUID().toString();

        username = "admin_" + uniqueId;
        password = "Admin@12345";

        requestBody = new HashMap<>();

        requestBody.put("username", username);
        requestBody.put("email", "admin_" + uniqueId + "@gmail.com");
        requestBody.put("password", password);
    }

    @When("I send a POST request to register admin API")
    public void sendAdminRegistrationRequest() {

        response = authApi.registerAdmin(requestBody);
    }


    @When("I send a POST request to admin login API")
    public void sendAdminLoginRequest() {

        Map<String, String> loginBody = new HashMap<>();

        loginBody.put("username", username);
        loginBody.put("password", password);

        response = authApi.login(loginBody);
    }

    @Then("the admin JWT should contain ROLE_ADMIN")
    public void validateAdminRole() throws Exception {

        String adminToken =
                response.jsonPath().getString("token");

        assertTrue(adminToken != null && !adminToken.isBlank(),
                "Admin JWT should not be empty");

        String[] parts = adminToken.split("\\.");

        assertEquals(parts.length, 3,
                "JWT should contain three parts");

        String decodedPayload = new String(
                Base64.getUrlDecoder().decode(parts[1])
        );

        JsonNode payload = objectMapper.readTree(decodedPayload);

        assertTrue(payload.has("roles"),
                "JWT should contain roles");

        assertTrue(
                payload.get("roles").toString().contains("ROLE_ADMIN"),
                "JWT should contain ROLE_ADMIN"
        );

        System.out.println("Admin JWT role: "
                + payload.get("roles"));
    }


    @When("I resend the same admin registration request")
    public void resendAdminRegistrationRequest() {

        response = authApi.registerAdmin(requestBody);

        System.out.println("Duplicate Registration Status: "
                + response.getStatusCode());

        System.out.println("Duplicate Registration Response:");
        response.prettyPrint();    }




}
