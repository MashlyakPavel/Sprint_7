package logincouriertest;
import CourierTest.CourierHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Courier Management")
@Feature("Courier Login")
public class LoginCourierAnotherTest {

    private Gson gson;
    private int courierId = -1;
    private CourierTest.CourierHelper courierHelper = new CourierHelper();

    @Before
    @Step("Set up test environment")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredLoginCourier() {
        String login = "infin";
        String password = "1111";
        Map<String, String> body = new HashMap<>();
        body.put("login", login);
        body.put("password", password);
        body.put("firstName", "pavel");
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());
        try {
            courierId = courierHelper.getCourierId(login, password);
            assertThat(courierId, is(not(-1)));
            String bodyWithoutLogin = "{ \"password\": \"1111\" }";
            Response responseWithoutLogin = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutLogin)
                    .when()
                    .post("/api/v1/courier/login");
            String expectedMessage = "Недостаточно данных для входа";
            assertThat(responseWithoutLogin.getStatusCode(), is(400));
            assertThat(responseWithoutLogin.jsonPath().getString("message"), is(expectedMessage));
            System.out.println("Тест на отсутствие логина. Код ответа: " + responseWithoutLogin.getStatusCode());
        } finally {
            if (courierId != -1) {
                Response deleteResponse = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .when()
                        .delete("/api/v1/courier/" + courierId);
                if (deleteResponse.getStatusCode() == 200) {
                    System.out.println("Курьер удален. Код ответа: " + deleteResponse.getStatusCode());
                } else {
                    System.err.println("Ошибка при удалении курьера. Код ответа: " + deleteResponse.getStatusCode());
                    System.err.println("Тело ответа: " + deleteResponse.asString());
                }
            } else {
                System.err.println("Ошибка: не удалось получить ID курьера, удаление невозможно.");
            }
        }
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredPasswordCourier() {
        String login = "infin";
        String password = "1111";
        Map<String, String> body = new HashMap<>();
        body.put("login", login);
        body.put("password", password);
        body.put("firstName", "pavel");
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        String expectedMessage = "Недостаточно данных для входа";
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());
        try {
            courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера
            assertThat(courierId, is(not(-1))); // Что ID не -1

            String bodyWithoutPassword = "{ \"login\": \"infin\" }"; // Существующий логин
            Response responseWithoutPassword = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutPassword)
                    .when()
                    .post("/api/v1/courier/login");
            assertThat(responseWithoutPassword.getStatusCode(), is(400));
            assertThat(responseWithoutPassword.jsonPath().getString("message"), is(expectedMessage));
            System.out.println("Тест на отсутствие пароля. Код ответа: " + responseWithoutPassword.getStatusCode());
        } finally {
            if (courierId != -1) {
                Response deleteResponse = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .when()
                        .delete("/api/v1/courier/" + courierId);
                if (deleteResponse.getStatusCode() == 200) {
                    System.out.println("Курьер удален. Код ответа: " + deleteResponse.getStatusCode());
                } else {
                    System.err.println("Ошибка при удалении курьера. Код ответа: " + deleteResponse.getStatusCode());
                    System.err.println("Тело ответа: " + deleteResponse.asString());
                }
            } else {
                System.err.println("Ошибка: не удалось получить ID курьера, удаление невозможно.");
            }
        }
    }
}


