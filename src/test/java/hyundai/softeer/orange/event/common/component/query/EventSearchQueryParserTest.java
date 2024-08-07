package hyundai.softeer.orange.event.common.component.query;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventSearchQueryParserTest {
    @DisplayName("query parser이 정상적으로 동작하는지 검사")
    @Test
    public void testParser() {
        // 1. key:value,key:value,key 형식으로 표현됨
        // 2. :으로 구분되는 부분은 1개 또는 2개여야 함.

        String searchQuery = "test1:value1,, \t\n,hello,test2:value2,wrong:a:b";

        Map<String, String> parsed = EventSearchQueryParser.parse(searchQuery);
        assertThat(parsed).hasSize(3);
        assertThat(parsed.get("test1")).isEqualTo("value1");
        assertThat(parsed.get("test2")).isEqualTo("value2");
        assertThat(parsed.get("hello")).isEqualTo("");
    }
}