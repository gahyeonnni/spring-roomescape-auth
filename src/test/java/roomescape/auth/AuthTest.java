package roomescape.auth;

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
class AuthTest {

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
    @DisplayName("미인증 사용자가 인증이 필요한 API 호출 시 401")
    void 미인증_예약_조회_실패() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("미인증 사용자가 예약 생성 시 401")
    void 미인증_예약_생성_실패() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("date", "2099-08-05", "timeId", 1, "themeId", 1))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("일반 사용자가 관리자 전용 API 호출 시 403")
    void 일반_유저_관리자_API_접근_실패() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", loginAsUser())
                .contentType(ContentType.JSON)
                .body(Map.of("startAt", "20:00", "finishAt", "21:00"))
                .when().post("/times")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("관리자는 관리자 전용 API 호출 가능")
    void 관리자_관리자_API_접근_성공() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", loginAsAdmin())
                .contentType(ContentType.JSON)
                .body(Map.of("startAt", "20:00", "finishAt", "21:00"))
                .when().post("/times")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("로그인 사용자의 role이 /users/me에서 올바르게 반환됨")
    void 로그인_사용자_정보_조회() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", loginAsUser())
                .when().get("/users/me")
                .then().log().all()
                .statusCode(200)
                .body("role", equalTo("USER"));
    }

    @Test
    @DisplayName("관리자 role이 /users/me에서 올바르게 반환됨")
    void 관리자_사용자_정보_조회() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", loginAsAdmin())
                .when().get("/users/me")
                .then().log().all()
                .statusCode(200)
                .body("role", equalTo("ADMIN"));
    }

    @Test
    @DisplayName("미인증 사용자가 /users/me 호출 시 401")
    void 미인증_사용자_정보_조회_실패() {
        RestAssured.given().log().all()
                .when().get("/users/me")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("로그인 사용자는 본인 예약만 조회됨")
    void 로그인_사용자_본인_예약_조회() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", loginAsUser())
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200);
    }
}
