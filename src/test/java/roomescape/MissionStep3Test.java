package roomescape;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStep3Test {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private String loginAsUser() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user1@test.com", "password", "password1"))
                .when().post("/users/login")
                .then().statusCode(200)
                .extract().cookie("JSESSIONID");
    }

    private String loginAsAdmin() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "admin@test.com", "password", "admin"))
                .when().post("/users/login")
                .then().statusCode(200)
                .extract().cookie("JSESSIONID");
    }

    @Test
    void 시간_관리_API() {
        String adminSession = loginAsAdmin();
        String userSession = loginAsUser();

        Map<String, String> params = new HashMap<>();
        params.put("startAt", "20:00");
        params.put("finishAt", "21:00");

        Integer newId = RestAssured.given().log().all()
            .cookie("JSESSIONID", adminSession)
            .contentType(ContentType.JSON)
            .body(params)
            .when().post("/times")
            .then().log().all()
            .statusCode(201)
            .extract().path("id");

        RestAssured.given().log().all()
            .cookie("JSESSIONID", userSession)
            .when().get("/times")
            .then().log().all()
            .statusCode(200)
            .body("size()", is(4));

        RestAssured.given().log().all()
            .cookie("JSESSIONID", adminSession)
            .when().delete("/times/" + newId)
            .then().log().all()
            .statusCode(204);
    }

    @Test
    void 예약과_시간_연결() {
        String session = loginAsUser();

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2099-08-05");
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);

        RestAssured.given().log().all()
            .cookie("JSESSIONID", session)
            .contentType(ContentType.JSON)
            .body(reservation)
            .when().post("/reservations")
            .then().log().all()
            .statusCode(201);

        RestAssured.given().log().all()
            .cookie("JSESSIONID", session)
            .when().get("/times/available?date=2099-08-05&themeId=1")
            .then().log().all()
            .statusCode(200)
            .body("size()", is(2));
    }
}
