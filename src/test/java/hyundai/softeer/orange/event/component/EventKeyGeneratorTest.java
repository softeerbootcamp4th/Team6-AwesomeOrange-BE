package hyundai.softeer.orange.event.component;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventKeyGeneratorTest {
    EventKeyGenerator eventKeyGenerator;
    StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString()))
        .thenAnswer(
        new Answer<Long>() {
            final Map<String, Long> map = new HashMap<>();
            @Override
            public Long answer(InvocationOnMock mock) throws Throwable {
                String key = mock.getArgument(0);
                Long value = map.getOrDefault(key, 1L);
                map.put(key, value + 1);
                return value;
            }
        });

        eventKeyGenerator = new EventKeyGenerator(redisTemplate);
    }

    @DisplayName("키가 제대로 생성되는지 검사")
    @Test
    void keyGenTest() {
        LocalDateTime testDate = LocalDateTime.of(2024,8,1,11,25,0);
        String key1 = eventKeyGenerator.generate(testDate);
        String key2 = eventKeyGenerator.generate(testDate);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");
        // 2024년 8월 1일 11시 25분
        String today = format.format(testDate);

        String expectedKey1 = "HD" + today + "_001";
        String expectedKey2 = "HD" + today + "_002";

        assertThat(key1).isEqualTo(expectedKey1);
        assertThat(key2).isEqualTo(expectedKey2);
        verify(redisTemplate, times(1))
                .expireAt(anyString(),any(Date.class));
    }
}