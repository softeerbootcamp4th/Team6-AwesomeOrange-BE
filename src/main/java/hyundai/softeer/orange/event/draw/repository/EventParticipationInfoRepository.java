package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.draw.entity.EventParticipationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipationInfoRepository extends JpaRepository<EventParticipationInfo, Long> {
}
