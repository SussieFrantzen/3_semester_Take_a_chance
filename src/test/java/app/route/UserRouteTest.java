
package app.route;
import app.config.HibernateTestConfig;
import app.config.TestServerConfig;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class UserRouteTest {

    private static Javalin app;

    private static final int TEST_PORT = 7071;

    @BeforeAll
    static void setUpAll() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = TEST_PORT;

        TestServerConfig serverConfig =
                new TestServerConfig();

        app = serverConfig.createServer();
        app.start(TEST_PORT);
    }

    @AfterAll
    static void afterAll() {
        if (app != null) {
            app.stop();
        }

        HibernateTestConfig.close();
        RestAssured.reset();
    }

    @Test
    void getUserWithoutLogin() {
        given()
                .when()
                .get("/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    void logoutWithoutUser() {
        given()
                .when()
                .post("/users/logout")
                .then()
                .statusCode(401);
    }
}
