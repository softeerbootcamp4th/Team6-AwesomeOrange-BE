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

        Page<EventMetadata> result = emRepository.findAll(spec, PageRequest.of(0,100));
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @DisplayName("search가 있으면 필터링 수행")
    @Test
    void searchWithSearchClause() {
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
        emRepository.save(em1);
        emRepository.save(em2);
        emRepository.save(em3);

        Specification<EventMetadata> spec = EventSpecification.searchOnName("event");

        Page<EventMetadata> result = emRepository.findAll(spec, PageRequest.of(0,100));
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}