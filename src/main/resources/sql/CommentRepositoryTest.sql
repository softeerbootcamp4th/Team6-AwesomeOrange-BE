INSERT INTO event_frame(name) VALUES ('test1');
INSERT INTO event_frame(name) VALUES ('test2');

INSERT INTO event_metadata(event_type, event_frame_id, event_id) VALUES (1, 1, 'HD_240808_001');
INSERT INTO event_metadata(event_type, event_frame_id, event_id) VALUES (0, 2, 'HD_240808_002');

INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 1, 'user1');
INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 1, 'user2');
INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 1, 'user3');

-- 3 comments for user1
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 1);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 1);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 1);

-- 6 comments for user2
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 2);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 2);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 2);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 2);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 2);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 2);

-- 2 comments for user3
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 3);
INSERT INTO comment(event_frame_id, event_user_id) VALUES (1, 3);
