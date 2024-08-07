package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class EventSpecificationTest {
    @Autowired
    EventFrameRepository efRepository;
    @Autowired
    EventMetadataRepository emRepository;

    @DisplayName("search가 없으면 predicate는 항상 참")
    @Test
    void searchWithoutSearchClause() {
        EventFrame ef = EventFrame.of("test");
        efRepository.save(ef);
        EventMetadata em1 = EventMetadata.builder()
                .eventId("HD240805_001")
                .name("hyundai car event")
                .eventFrame(ef)
                .build();

        EventMetadata em2 = EventMetadata.builder()
                .eventId("HD240805_002")
                .name("hello HD bye")
                .eventFrame(ef)
                .build();

        EventMetadata em3 = EventMetadata.builder()
                .eventId("HD240805_003")
                .name("hyundai car event2")
                .eventFrame(ef)
                .build();
        emRepository.save(em1);
        emRepository.save(em2);
        emRepository.save(em3);

        Specification<EventMetadata> spec = EventSpecification.searchOnName(null);
        Specification<EventMetadata> spec2 = EventSpecification.searchOnEventId(null);

        Page<EventMetadata> result = emRepository.findAll(spec.or(spec2), PageRequest.of(0,100));
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @DisplayName("search가 있으면 필터링 수행")
    @Test
    void searchWithSearchClause() {
        String search = "event";
        EventFrame ef = EventFrame.of("test");
        efRepository.save(ef);
        // event 포함 ( name )
        EventMetadata em1 = EventMetadata.builder()
                .eventId("HD240805_001")
                .name("hyundai car event")
                .eventFrame(ef)
                .build();

        // event 포함 ( eventId )
        EventMetadata em2 = EventMetadata.builder()
                .eventId("event011")
                .name("hello HD bye")
                .eventFrame(ef)
                .build();

        // event 포함 ( name )
        EventMetadata em3 = EventMetadata.builder()
                .eventId("HD240805_003")
                .name("hyundai car event hello world")
                .eventFrame(ef)
                .build();

        // event 포함 안됨
        EventMetadata em4 = EventMetadata.builder()
                .eventId("HD240805_004")
                .name("not included")
                .eventFrame(ef)
                .build();
        emRepository.saveAll(List.of(em1, em2, em3, em4));

        Specification<EventMetadata> spec1 = EventSpecification.searchOnName(search);
        Specification<EventMetadata> spec2 = EventSpecification.searchOnEventId(search);

        Page<EventMetadata> result = emRepository.findAll(spec1.or(spec2), PageRequest.of(0,100));

        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}