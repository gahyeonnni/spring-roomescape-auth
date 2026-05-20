package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("로그인 성공 시 200과 X-Session-Id 헤더 반환")
    void 로그인_성공() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user1@test.com", "password", "password1"))
                .when().post("/users/login")
                .then().log().all()
                .statusCode(200)
                .header("X-Session-Id", org.hamcrest.Matchers.notNullValue());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 401")
    void 로그인_실패_잘못된_비밀번호() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user1@test.com", "password", "wrongpassword"))
                .when().post("/users/login")
                .then().log().all()
                .statusCode(401)
                .body("errorCode", equalTo("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 401")
    void 로그인_실패_존재하지_않는_이메일() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "nobody@test.com", "password", "password1"))
                .when().post("/users/login")
                .then().log().all()
                .statusCode(401)
                .body("errorCode", equalTo("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("로그아웃 성공 시 200")
    void 로그아웃_성공() {
        String session = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user1@test.com", "password", "password1"))
                .when().post("/users/login")
                .then().statusCode(200)
                .extract().header("X-Session-Id");

        RestAssured.given().log().all()
                .header("X-Session-Id", session)
                .when().post("/users/logout")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그아웃 후 인증이 필요한 API 접근 시 401")
    void 로그아웃_후_인증_필요한_API_접근_실패() {
        String session = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user1@test.com", "password", "password1"))
                .when().post("/users/login")
                .then().statusCode(200)
                .extract().header("X-Session-Id");

        RestAssured.given()
                .header("X-Session-Id", session)
                .when().post("/users/logout")
                .then().statusCode(200);

        RestAssured.given().log().all()
                .header("X-Session-Id", session)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(401);
    }
}
