
package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;
import specs.RequestSpec;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthSteps {

    private String username;
    private String email;
    private String password;
    private Response response;

    @Given("I have valid registration details")
    public void validRegistrationDetails() {

        String uniqueId = String.valueOf(System.currentTimeMillis());

        username = "testuser" + uniqueId;
        email = "test" + uniqueId + "@gmail.com";
        password = "Test@12345";

    }

    @When("I send a POST request to register the user")
    public void registerUser() {

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .body(Map.of(
                        "username", username,
                        "email", email,
                        "password", password
                ))
                .when()
                .post("/auth/register");

        response.prettyPrint();
    }

    @Then("the registration response status should be {int}")
    public void verifyStatusCode(int statusCode) {

        Assert.assertEquals(
                response.getStatusCode(),
                statusCode
        );
    }

    @Then("the response message should be {string}")
    public void verifyResponseMessage(String expectedMessage) {

        Assert.assertEquals(
                response.asString(),
                expectedMessage
        );
    }
}
