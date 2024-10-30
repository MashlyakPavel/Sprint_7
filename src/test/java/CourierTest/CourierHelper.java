
package CourierTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class CourierHelper {
    private Gson gson;

    public CourierHelper() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }
    public String formatResponseBody(String responseBody) {
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        return gson.toJson(jsonElement);
    }

    public void checkStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode(), is(expectedStatusCode));
    }

    public void checkErrorMessage(Response response, String expectedMessage) {
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    public String createRequestBody(String login, String password, String firstName) {
        Map<String, String> courierMap = new HashMap<>();
        courierMap.put("login", login);
        courierMap.put("password", password);
        courierMap.put("firstName", firstName);
        return gson.toJson(courierMap);
    }

    @Step("create courier")
    public Response createCourier(String body) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
    }

    public int authorizeCourier(String login, String password) {
        int courierId = getCourierId(login, password);
        assertThat(courierId, is(not(-1)));
        return courierId;
    }

    public int getCourierId(String login, String password) {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .when()
                .post("/api/v1/courier/login");

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getInt("id");
        } else {
            return -1;
        }
    }

    public void deleteCourier(int courierId) {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
        System.out.println("Курьер с ID " + courierId + " был удален.");
    }

    public void printResponse(Response response, Gson gson) {
        String responseBody = response.getBody().asString();
        String formattedJson = gson.toJson(gson.fromJson(responseBody, Object.class));
        System.out.println("Код ответа: " + response.getStatusCode());
        System.out.println("Тело ответа: " + formattedJson);
    }
}
