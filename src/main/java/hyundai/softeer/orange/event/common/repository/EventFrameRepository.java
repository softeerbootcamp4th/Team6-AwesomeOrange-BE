package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventFrameRepository extends JpaRepository<EventFrame, Long> {
    Optional<EventFrame> findByName(String name);
}
