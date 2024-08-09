package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrawEventRepository extends JpaRepository<DrawEvent, Long> {
    @Query(value = "SELECT d FROM DrawEvent d WHERE d.eventMetadata.eventId = :eventId")
    Optional<DrawEvent> findByEventId(@Param("eventId") String eventId);
}
