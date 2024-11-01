package logincouriertest;
import couriertest.CourierHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Epic("Courier Management")
@Feature("Courier Login")
public class LoginCourierTest {

    private Gson gson;

    private int courierId = -1;
    private couriertest.CourierHelper courierHelper = new CourierHelper();

    @After
    public void tearDown() {
        if (courierId != -1) {
            courierHelper.deleteCourier(courierId);
        }
    }

    @Before
    @Step("Set up test environment")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    @DisplayName("Courier can be created and login")
    @Step("Create courier and verify login")
    public void testCourierCanBeCreatedAndLogin() {
        // Данные для создания курьера
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
        String loginBody = "{ \"login\": \"infin\", \"password\": \"1111\" }";
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/v1/courier/login");
        assertThat(loginResponse.getStatusCode(), is(200));
        assertThat(loginResponse.jsonPath().get("id"), is(notNullValue()));
        System.out.println("Код ответа на авторизацию: " + loginResponse.getStatusCode());
        System.out.println("Тело ответа на авторизацию: " + loginResponse.asString());
        courierId = courierHelper.getCourierId(login, password);
        assertThat(courierId, is(not(-1)));
    }

    @Test
    @DisplayName("Login with wrong credentials should fail")
    @Step("Test courier login with wrong credentials")
    public void testWithWrongLoginOrPasswordCourier() {
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
        System.out.println("Курьер создан. Код ответа: " + createResponse.getStatusCode());
        String bodyWrongCredentials = "{ \"login\": \"sdgweg\", \"password\": \"2222\" }";
        Response responseWrongCredentials = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongCredentials)
                .when()
                .post("/api/v1/courier/login");
        assertThat(responseWrongCredentials.getStatusCode(), is(404));
        assertThat(responseWrongCredentials.jsonPath().getString("message"), is("Учетная запись не найдена"));
        System.out.println("Wrong login and password:");
        System.out.println("Response Code: " + responseWrongCredentials.getStatusCode());
        System.out.println("Response Body: " + responseWrongCredentials.asString());

        String bodyWrongLogin = "{ \"login\": \"sdgsdg\", \"password\": \"1111\" }";
        Response responseWrongLogin = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongLogin)
                .when()
                .post("/api/v1/courier/login");
        assertThat(responseWrongLogin.getStatusCode(), is(404));
        assertThat(responseWrongLogin.jsonPath().getString("message"), is("Учетная запись не найдена"));
        System.out.println("Wrong login:");
        System.out.println("Response Code: " + responseWrongLogin.getStatusCode());
        System.out.println("Response Body: " + responseWrongLogin.asString());

        String bodyWrongPassword = "{ \"login\": \"infin\", \"password\": \"3434\" }"; // Существующий логин
        Response responseWrongPassword = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongPassword)
                .when()
                .post("/api/v1/courier/login");
        assertThat(responseWrongPassword.getStatusCode(), is(404));
        assertThat(responseWrongPassword.jsonPath().getString("message"), is("Учетная запись не найдена"));
        System.out.println("Wrong password:");
        System.out.println("Response Code: " + responseWrongPassword.getStatusCode());
        System.out.println("Response Body: " + responseWrongPassword.asString());
        courierId = courierHelper.getCourierId(login, password);
        assertThat(courierId, is(not(-1))); // Что ID не -1
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredFieldsCourier() {
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

            String bodyWithoutPassword = "{ \"login\": \"infin\" }";
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

    @Test
    @DisplayName("Login non-existent user returns error")
    @Step("Test login for non-existent courier")
    public void testLoginNonExistentUser() {
        Map<String, String> bodyNonExistentUser = new HashMap<>();
        bodyNonExistentUser.put("login", "qfdafasf");
        bodyNonExistentUser.put("password", "4313112");
        bodyNonExistentUser.put("firstName", "pavel");
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyNonExistentUser)
                .when()
                .post("/api/v1/courier/login");
        assertThat(response.getStatusCode(), is(404));
        assertThat(response.jsonPath().getString("message"), is("Учетная запись не найдена"));
        System.out.println("Not existent user login:");
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.asString());
    }
}

