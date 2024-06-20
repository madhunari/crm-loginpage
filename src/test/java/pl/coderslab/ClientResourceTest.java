package pl.coderslab;

import java.io.IOException;
import java.io.InputStream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.internal.util.IOUtils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@TestPropertySource("/test.properties")
@SpringBootTest(classes = { CrmApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class ClientResourceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void should_create_client() throws IOException {
        // given
        byte[] body = loadResource("/Client.json");
        RequestSpecification request = RestAssured.given()
                .body(body)
                .header(new Header("Content-Type", "application/json"));
        // when
        Response response = request.post(getMailEndpoint());
        // then
        response.then().statusCode(200);
    }

    @Test
    public void should_return_created_user() throws IOException {
        // given
        byte[] body = loadResource("/Client.json");
        String id = RestAssured.given()
                .body(body)
                .header(new Header("Content-Type", "application/json"))
                .post(getMailEndpoint())
                .getBody().asString();
        // when
        Response response = RestAssured.given()
                .auth().basic("kowalski@gmail.com", "pass")
                .get(getIdEndpoint() + id);
        // then
        response.then().statusCode(200)
                .body("name", Matchers.equalTo("Coderslab"))
                .body("status", Matchers.equalTo("lead"))
                .body("contactPerson.lastname", Matchers.equalTo("Nowak"))
                .body("address.city", Matchers.equalTo("Warsaw"));
    }

    private String getIdEndpoint() {
        return "/clients/";
    }

    private String getMailEndpoint() {
        return "/clients/test@mail.pl";
    }

    private byte[] loadResource(String resourcePath) throws IOException {
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream(resourcePath)) {
            return IOUtils.toByteArray(resourceAsStream);
        }
    }
}
