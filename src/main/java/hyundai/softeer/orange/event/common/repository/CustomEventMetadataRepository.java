package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomEventMetadataRepository {
    List<EventMetadata> findAllBySearch(String search, Pageable pageable);
}
