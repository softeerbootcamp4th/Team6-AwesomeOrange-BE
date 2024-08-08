package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.draw.dto.DrawEventWinningInfoBulkInsertDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@TestPropertySource(locations = "classpath:application-test.yml")
class CustomDrawEventWinningInfoRepositoryImplTest {
    @Autowired // DrawEventWinningInfoRepository로 접근 가능해야 함
    private DrawEventWinningInfoRepository repo;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("INSERT INTO event_frame(name) VALUES ('test')");
        jdbcTemplate.execute("INSERT INTO event_metadata(event_type,event_frame_id,event_id) values (1, 1, 'HD_240808_001')");
        jdbcTemplate.execute("INSERT INTO draw_event values ()");
        jdbcTemplate.execute("INSERT INTO event_metadata(event_type,event_frame_id,event_id) values (1, 1, 'HD_240808_002')");
        jdbcTemplate.execute("INSERT INTO draw_event values ()");
        jdbcTemplate.execute("INSERT INTO event_user(score, event_frame_id, user_id) values ( 0, 1, 'user1')");
        jdbcTemplate.execute("INSERT INTO event_user(score, event_frame_id, user_id) values ( 0, 1, 'user2')");
        jdbcTemplate.execute("INSERT INTO event_user(score, event_frame_id, user_id) values ( 0, 1, 'user3')");
        jdbcTemplate.execute("INSERT INTO event_user(score, event_frame_id, user_id) values ( 0, 1, 'user4')");
        jdbcTemplate.execute("INSERT INTO event_user(score, event_frame_id, user_id) values ( 0, 1, 'user5')");
        jdbcTemplate.execute("INSERT INTO event_user(score, event_frame_id, user_id) values ( 0, 1, 'user6')");
    }


    @DisplayName("제대로 데이터를 삽입하는지 확인")
    @Test
    void testBulkInsertWork() {
        // Executing prepared SQL statement [insert into draw_event_winning_info (event_user_id, ranking, draw_event_id) VALUES (?, ?, ?)]

        List<DrawEventWinningInfoBulkInsertDto> targets = List.of(
                DrawEventWinningInfoBulkInsertDto.of(1, 1, 1),
                DrawEventWinningInfoBulkInsertDto.of(2, 1, 1),
                DrawEventWinningInfoBulkInsertDto.of(3, 2, 1),
                DrawEventWinningInfoBulkInsertDto.of(4, 2, 1),
                DrawEventWinningInfoBulkInsertDto.of(5, 2, 1),
                DrawEventWinningInfoBulkInsertDto.of(6, 2, 1),
                DrawEventWinningInfoBulkInsertDto.of(1, 1, 2),
                DrawEventWinningInfoBulkInsertDto.of(2, 1, 2),
                DrawEventWinningInfoBulkInsertDto.of(3, 2, 2),
                DrawEventWinningInfoBulkInsertDto.of(4, 2, 2),
                DrawEventWinningInfoBulkInsertDto.of(5, 2, 2),
                DrawEventWinningInfoBulkInsertDto.of(6, 2, 2)
        );
        repo.insertMany(targets);
        var events = repo.findAll();
        assertThat(events).hasSize(12);
    }
}