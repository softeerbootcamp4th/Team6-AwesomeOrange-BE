package hyundai.softeer.orange.eventuser.repository;

import hyundai.softeer.orange.eventuser.dto.EventUserScoreDto;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventUserRepository extends JpaRepository<EventUser, Long>, CustomEventUserRepository {

    Optional<EventUser> findByUserNameAndPhoneNumber(String userName, String phoneNumber);

    Optional<EventUser> findByUserId(String userId);

    @Query("SELECT eu FROM EventUser eu WHERE eu.userId IN :userIds")
    List<EventUser> findAllByUserId(@Param("userIds") List<String> userIds);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT u.id as userId, u.score as score FROM event_user u " +
            "JOIN event_frame ef ON ef.id = u.event_frame_id " +
            "JOIN event_metadata e ON e.event_frame_id = ef.id " +
            "WHERE e.id = :rawEventId", nativeQuery = true)
    List<EventUserScoreDto> findAllUserScoreByDrawEventId(@Param("rawEventId") long rawEventId);
}
