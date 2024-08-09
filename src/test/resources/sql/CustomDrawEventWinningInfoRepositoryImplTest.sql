INSERT INTO event_frame(name) VALUES ('test');

INSERT INTO event_metadata(event_type, event_frame_id, event_id)
VALUES (1, 1, 'HD_240808_001');

INSERT INTO draw_event(event_metadata_id)
VALUES (1);

INSERT INTO event_metadata(event_type, event_frame_id, event_id)
VALUES (1, 1, 'HD_240808_002');

INSERT INTO draw_event(event_metadata_id)
VALUES (2);

INSERT INTO event_user(score, event_frame_id, user_id)
VALUES (0, 1, 'user1');

INSERT INTO event_user(score, event_frame_id, user_id)
VALUES (0, 1, 'user2');

INSERT INTO event_user(score, event_frame_id, user_id)
VALUES (0, 1, 'user3');

INSERT INTO event_user(score, event_frame_id, user_id)
VALUES (0, 1, 'user4');

INSERT INTO event_user(score, event_frame_id, user_id)
VALUES (0, 1, 'user5');

INSERT INTO event_user(score, event_frame_id, user_id)
VALUES (0, 1, 'user6');