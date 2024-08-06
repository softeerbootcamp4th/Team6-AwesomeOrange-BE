package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventMetadataRepository extends JpaRepository<EventMetadata, Long>, JpaSpecificationExecutor<EventMetadata> {
    Optional<EventMetadata> findFirstByEventId(String eventId);
}
