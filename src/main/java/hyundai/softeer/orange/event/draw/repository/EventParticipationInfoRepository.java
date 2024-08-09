package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.draw.dto.EventParticipateCountDto;
import hyundai.softeer.orange.event.draw.entity.EventParticipationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventParticipationInfoRepository extends JpaRepository<EventParticipationInfo, Long> {
    @Query(value = "SELECT event_user_id as eventUserId, COUNT(event_user_id) as count " +
            "FROM event_partication_info " +
            "WHERE draw_event_id = :eventRawId " +
            "GROUP BY event_user_id", nativeQuery = true)
    List<EventParticipateCountDto> countPerEventUserByEventId(Long eventRawId);
}
