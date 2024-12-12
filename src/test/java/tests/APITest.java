package tests;

import api.ApiService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class APITest {
    WebDriver driver;
    public static void main(String[] args) {
        APITest apiTest = new APITest();
        apiTest.apiTest();
        apiTest.restAssureTest();
    }
    public void apiTest(){
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        Response response = RestAssured
                .given()
                .get("/posts/1");

        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }
    public void restAssureTest(){
        // Call the authenticate method
        Response response = ApiService.authenticate("testuser", "testpassword");

        // Print the response details
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }
    @Test
    public void testLoginWithApiSetup() {
        Response response = ApiService.authenticate("user", "pass");
        String authToken = response.jsonPath().getString("token");
        driver.findElement(By.id("auth-token-field")).sendKeys(authToken);
    }

}
