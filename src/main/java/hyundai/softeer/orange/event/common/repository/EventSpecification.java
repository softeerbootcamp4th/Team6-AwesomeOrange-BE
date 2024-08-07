package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

    public static Specification<EventMetadata> searchOnName(String search) {
        return searchOnName(search, true);
    }

    public static Specification<EventMetadata> searchOnName(String search, boolean conjunctionOnNull) {
        return (metadata, query, cb) -> {
            if (search == null || search.isEmpty()) return (conjunctionOnNull ? cb.conjunction() : cb.disjunction());
            return cb.like(metadata.get("name"), "%" + search + "%");
        };
    }

    public static Specification<EventMetadata> searchOnEventId(String search) {
        return searchOnEventId(search, true);
    }

    public static Specification<EventMetadata> searchOnEventId(String search,  boolean conjunctionOnNull) {
        return (metadata, query, cb) -> {
            if (search == null || search.isEmpty()) return (conjunctionOnNull ? cb.conjunction() : cb.disjunction());
            return cb.like(metadata.get("eventId"), "%" + search + "%");
        };
    }

    public static Specification<EventMetadata> isEventTypeOf(EventType eventType) {
        return (metadata, query, cb) -> cb.equal(metadata.get("eventType"), eventType);
    }
}
