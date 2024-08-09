INSERT INTO event_frame(name) VALUES ('test1');
INSERT INTO event_frame(name) VALUES ('test2');
INSERT INTO event_frame(name) VALUES ('test3');

INSERT INTO event_metadata(event_type, event_frame_id, event_id) VALUES (1, 1, 'HD_240808_001');
INSERT INTO event_metadata(event_type, event_frame_id, event_id) VALUES (1, 2, 'HD_240808_002');
INSERT INTO event_metadata(event_type, event_frame_id, event_id) VALUES (1, 3, 'HD_240808_003');

INSERT INTO draw_event(event_metadata_id) VALUES(1);

INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 1, 'user1');
INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 1, 'user2');
INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 1, 'user3');
INSERT INTO event_user(score, event_frame_id, user_id) VALUES (0, 3, 'user4');

INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,1);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,1);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,1);

INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,2);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,2);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,2);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,2);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,2);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,2);

INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,3);
INSERT INTO event_partication_info(draw_event_id, event_user_id) VALUES (1,3);
