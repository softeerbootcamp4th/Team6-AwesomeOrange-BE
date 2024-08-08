package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.draw.dto.DrawEventWinningInfoBulkInsertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomDrawEventWinningInfoRepositoryImpl implements CustomDrawEventWinningInfoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertMany(List<DrawEventWinningInfoBulkInsertDto> targets) {
        String sql = "insert into draw_event_winning_info (event_user_id, ranking, draw_event_id) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var target = targets.get(i);
                        ps.setLong(1, target.getEventUserId());
                        ps.setLong(2, target.getRanking());
                        ps.setLong(3, target.getDrawEventId());
                    }

                    @Override
                    public int getBatchSize() {
                        return targets.size();
                    }
                }
        );
    }
}
