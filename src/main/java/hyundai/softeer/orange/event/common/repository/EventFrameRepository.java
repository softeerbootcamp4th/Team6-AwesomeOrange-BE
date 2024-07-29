package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventFrameRepository extends JpaRepository<EventFrame, Long> {
}
