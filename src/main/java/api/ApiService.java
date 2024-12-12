package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiService {

    private static final String BASE_URL = "https://example.com";

    /**
     * Authenticates the user with the given username and password.
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @return the Response object containing the server's response
     */
    public static Response authenticate(String username, String password) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(createAuthPayload(username, password))
                .post("/auth/login");
    }

    /**
     * Creates the authentication payload as a JSON string.
     *
     * @param username the username
     * @param password the password
     * @return a JSON string representing the authentication payload
     */
    private static String createAuthPayload(String username, String password) {
        return String.format("{ \"username\": \"%s\", \"password\": \"%s\" }", username, password);
    }
}
