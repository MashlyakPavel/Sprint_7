package CreateOrderTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private String deliveryDate;
    private String comment;
    private String[] color;
    private int rentTime;

    String orderId;
    public CreateOrderTest(String firstName, String lastName, String address, String metroStation,
                           String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                { "asdwe", "asdwe", "msk, 7 apt.", "43", "+7 900 123 45 67", 4, "2024-11-23", "Call before", new String[] { "GRAY" } },
                { "asdwe", "asdwe", "msk, 7 apt.", "43", "+7 900 123 45 67", 4, "2024-11-23","Call before", new String[] { "GRAY", "BLACK" } },
                { "asdwe", "asdwe", "msk, 7 apt.", "43", "+7 900 123 45 67", 4, "2024-11-23", "Call before", new String[] { } },
                { "asdwe", "asdwe", "msk, 7 apt.", "43", "+7 900 123 45 67", 4, "2024-11-23", "Call before", new String[] { "BLACK" } },
        };
    }

    @Test
    @DisplayName("Creating an order with different colors")
    public void createOrderParameterizedColorScooterTest() {
        OrderCreate orderCreate = new OrderCreate(firstName, lastName, address,
                metroStation, phone, deliveryDate, comment, color, rentTime);
        Response createResponse = OrderClient.createNewOrder(orderCreate);
        OrderClient.comparingSuccessfulOrderSet(createResponse, SC_CREATED);
    }
}

