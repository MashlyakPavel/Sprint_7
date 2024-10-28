package CourierTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Courier Management")
@Feature("Courier Creation")
public class CourierAnotherTest {

    private static final String DEFAULT_LOGIN = "infin";
    private static final String DEFAULT_PASSWORD = "1111";
    private static final String DEFAULT_FIRST_NAME = "pavel";

    private Gson gson;

    private int courierId = -1;
    private CourierHelper courierHelper = new CourierHelper();

    @After
    public void tearDown() {
        if (courierId != -1) {
            courierHelper.deleteCourier(courierId);
        }
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        String body = courierHelper.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierHelper.createCourier(body);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body: " + formattedResponseBody);
        System.out.println("Status code: " + response.getStatusCode());
        courierHelper.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierHelper.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        String body = courierHelper.createRequestBody("infin", "1111", "pavel");
        Response response = courierHelper.createCourier(body);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body: " + formattedResponseBody);
        courierHelper.checkStatusCode(response, 201);
        System.out.println("Курьер успешно создан. Код ответа: " + response.getStatusCode());
        Response secondResponse = courierHelper.createCourier(body);
        String formattedSecondResponseBody = courierHelper.formatResponseBody(secondResponse.getBody().asString());
        System.out.println("Formatted response body (duplicate creation attempt): " + formattedSecondResponseBody);
        courierHelper.checkStatusCode(secondResponse, 409);
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        courierHelper.checkErrorMessage(secondResponse, expectedMessage);
        courierId = courierHelper.authorizeCourier("infin", "1111");
        assertThat(courierId, is(not(-1)));
    }

    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {
        String body = courierHelper.createRequestBody("infin", "1111", "pavel");
        Response response = courierHelper.createCourier(body);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body: " + formattedResponseBody);
        System.out.println("Status code: " + response.getStatusCode());
        courierHelper.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierHelper.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    @Test
    @Story("Validate status code 201 for successful courier creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that creating a new courier returns status code 201")
    public void testCreateCourierCode201() {
        String body = courierHelper.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierHelper.createCourier(body);
        System.out.println("Status code: " + response.getStatusCode());
        courierHelper.checkStatusCode(response, 201);
        courierId = courierHelper.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    @Test
    @Story("Validate 'ok: true' for successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a successful courier creation returns 'ok: true' in the response")
    public void testCreateCourierOkTrue() {
        String body = courierHelper.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierHelper.createCourier(body);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body: " + formattedResponseBody);
        courierId = courierHelper.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        String bodyWithoutLogin = "{ \"password\": \"1111\", \"firstName\": \"pavel\" }";
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        Response response = courierHelper.createCourier(bodyWithoutLogin);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body (missing login): " + formattedResponseBody);
        courierHelper.checkStatusCode(response, 400);
        System.out.println("Курьер не создан: пропущено поле login");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {
        String bodyWithoutLogin = "{ \"login\": \"infin\", \"firstName\": \"pavel\" }";
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        Response response = courierHelper.createCourier(bodyWithoutLogin);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body (missing login): " + formattedResponseBody);
        courierHelper.checkStatusCode(response, 400);
        System.out.println("Курьер не создан: пропущено поле password");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a first name returns an error")
    public void testCreateCourierWithoutFirstName() {
        String bodyWithoutLogin = "{ \"login\": \"infin\", \"password\": \"1111\" }";
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        Response response = courierHelper.createCourier(bodyWithoutLogin);
        String formattedResponseBody = courierHelper.formatResponseBody(response.getBody().asString());
        System.out.println("Formatted response body (missing login): " + formattedResponseBody);
        courierHelper.checkStatusCode(response, 400);
        System.out.println("Курьер не создан: пропущено поле firstName");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
}

