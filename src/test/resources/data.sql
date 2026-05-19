INSERT INTO reservation_time (start_at, finish_at) VALUES ('10:00', '11:00');
INSERT INTO reservation_time (start_at, finish_at) VALUES ('14:00', '15:00');
INSERT INTO reservation_time (start_at, finish_at) VALUES ('18:00', '19:00');

INSERT INTO theme (name, description, image_url) VALUES ('테마A', '설명A', 'https://a.com');
INSERT INTO theme (name, description, image_url) VALUES ('테마B', '설명B', 'https://b.com');
INSERT INTO theme (name, description, image_url) VALUES ('테마C', '설명C', 'https://c.com');
INSERT INTO theme (name, description, image_url) VALUES ('테마D', '설명D', 'https://d.com');

INSERT INTO "user" (name, email, password, role) VALUES ('user1', 'user1@test.com', 'password1', 'USER');
INSERT INTO "user" (name, email, password, role) VALUES ('user2', 'user2@test.com', 'password2', 'USER');
INSERT INTO "user" (name, email, password, role) VALUES ('user3', 'user3@test.com', 'password3', 'USER');
INSERT INTO "user" (name, email, password, role) VALUES ('user4', 'user4@test.com', 'password4', 'USER');
INSERT INTO "user" (name, email, password, role) VALUES ('user5', 'user5@test.com', 'password5', 'USER');
INSERT INTO "user" (name, email, password, role) VALUES ('admin', 'admin@test.com', 'admin', 'ADMIN');

-- id 1: 과거 (past cancel/update 테스트)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-10', 1, 1, 1);
-- id 2
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-09', 1, 1, 2);
-- id 3-5: 최근 7일, 테마A (인기 테마 테스트 - 3건)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-14', 1, 1, 3);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-13', 1, 1, 4);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-12', 1, 1, 5);
-- id 6-7: 최근 7일, 테마B (인기 테마 테스트 - 2건)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-14', 1, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-13', 1, 2, 2);
-- id 8: 최근 7일, 테마C (인기 테마 테스트 - 1건)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-14', 2, 3, 1);
-- id 9: 7일 범위 밖, 테마D
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-01', 1, 4, 1);
-- id 10: 예약 가능 시간 조회 테스트용 (2026-05-10, theme 1, time 1 은 id 1로 이미 예약됨)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-10', 2, 2, 1);
-- id 11: 미래 (update/delete 테스트)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2099-12-01', 1, 1, 1);
-- id 12: 미래 (중복 update 테스트)
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2099-12-01', 2, 1, 2);
-- id 13-15: ThemeRepositoryTest 범위 (2026-05-03 ~ 2026-05-10) 인기 테마 순위용
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-08', 1, 1, 3);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-07', 1, 2, 3);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2026-05-05', 1, 3, 3);
