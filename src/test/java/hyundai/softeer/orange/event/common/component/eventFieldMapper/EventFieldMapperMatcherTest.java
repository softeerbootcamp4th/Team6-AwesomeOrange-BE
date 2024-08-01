package hyundai.softeer.orange.event.common.component.eventFieldMapper;

import hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper.DrawEventFieldMapper;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper.EventFieldMapper;
import hyundai.softeer.orange.event.common.enums.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventFieldMapperMatcherTest {
    @DisplayName("이벤트 타입에 대응되는 매퍼가 있다면 반환한다. 없다면 null을 반환한다.")
    @Test
    void returnMapperIfExistElseNull() {
        EventType existType = EventType.draw;
        EventType notExistType = EventType.fcfs;
        List<EventFieldMapper> mappers = List.of(new DrawEventFieldMapper());
        EventFieldMapperMatcher mapperMatcher = new EventFieldMapperMatcher(mappers);

        var mapper1 = mapperMatcher.getMapper(existType);
        var mapper2 = mapperMatcher.getMapper(notExistType);

        assertThat(mapper1).isNotNull().isInstanceOf(DrawEventFieldMapper.class);
        assertThat(mapper2).isNull();
    }
}