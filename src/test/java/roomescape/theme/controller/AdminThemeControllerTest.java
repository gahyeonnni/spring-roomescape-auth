package roomescape.theme.controller;

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
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminThemeControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private String loginAsAdmin() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "admin@test.com", "password", "admin"))
                .when().post("/users/login")
                .then().statusCode(200)
                .extract().header("X-Session-Id");
    }

    private Map<String, String> themeBody() {
        return Map.of("name", "테마5", "description", "설명", "imageUrl", "https://image.com");
    }

    @Test
    @DisplayName("테마 생성 성공")
    void 테마_생성_성공() {
        RestAssured.given().log().all()
                .header("X-Session-Id", loginAsAdmin())
                .contentType(ContentType.JSON)
                .body(themeBody())
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201)
                .body("name", equalTo("테마5"));
    }

    @Test
    @DisplayName("테마 전체 조회 성공")
    void 테마_전체_조회_성공() {
        RestAssured.given().log().all()
                .header("X-Session-Id", loginAsAdmin())
                .when().get("/admin/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @Test
    @DisplayName("테마 삭제 성공")
    void 테마_삭제_성공() {
        String adminCookie = loginAsAdmin();
        Integer id = RestAssured.given().log().all()
                .header("X-Session-Id", adminCookie)
                .contentType(ContentType.JSON)
                .body(themeBody())
                .when().post("/admin/themes")
                .then().extract().path("id");

        RestAssured.given().log().all()
                .header("X-Session-Id", adminCookie)
                .when().delete("/admin/themes/" + id)
                .then().log().all()
                .statusCode(204);
    }
}
