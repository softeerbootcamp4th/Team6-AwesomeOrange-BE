package hyundai.softeer.orange.eventuser.repository;

import hyundai.softeer.orange.eventuser.dto.EventUserScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomEventUserRepositoryImpl implements CustomEventUserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void updateScoreMany(List<EventUserScoreDto> userScores) {
        String sql = "UPDATE event_user SET score = ? WHERE id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                EventUserScoreDto userScore = userScores.get(i);
                ps.setLong(1, userScore.score());
                ps.setLong(2, userScore.userId());
            }

            @Override
            public int getBatchSize() {
                return userScores.size();
            }
        });
    }
}
