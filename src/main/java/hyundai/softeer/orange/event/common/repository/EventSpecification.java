package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {
    public static Specification<EventMetadata> withSearch(String search) {
        return (metadata, query, cb) -> {
            if (search == null || search.isEmpty()) return cb.conjunction();

            Predicate searchName = cb.like(metadata.get("name"), "%" + search + "%");
            Predicate searchEventId = cb.like(metadata.get("eventId"), "%" + search + "%");
            return cb.or(searchName, searchEventId);
        };
    }
}
