package hyundai.softeer.orange.event.component;

import hyundai.softeer.orange.common.util.DateUtil;
import hyundai.softeer.orange.event.common.EventConst;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventKeyGenerator {
    private final DateTimeFormatter formatter;
    private final StringRedisTemplate redisTemplate;

    public EventKeyGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.formatter = DateTimeFormatter.ofPattern("yyMMdd");
    }

    public String generate() {
        return generate(LocalDateTime.now());
    }

    public String generate(LocalDateTime now) {
        LocalDateTime nextDay = now.plusDays(1).toLocalDate().atTime(0,0,5);

        String dateInfo = formatter.format(now);
        String incKey = EventConst.REDIS_KEY_PREFIX + dateInfo;

        Long number = redisTemplate.opsForValue().increment(incKey);

        if(number != null && number == 1)
            redisTemplate.expireAt(incKey, DateUtil.localDateTimeToDate(nextDay));

        return String.format("HD%s_%03d", dateInfo, number);
    }
}
