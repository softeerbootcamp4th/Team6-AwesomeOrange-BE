package hyundai.softeer.orange.event.draw;

import hyundai.softeer.orange.event.draw.component.score.actionHandler.ActionHandler;
import hyundai.softeer.orange.event.draw.enums.DrawEventAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class DrawEventConfig {
    private static final String NamePattern = "(.+?)_ActionHandler";

    // actionHandler을 Action 타입으로 가져올 수 있도록 설정
    @Bean(name = "actionHandlerMap")
    public Map<DrawEventAction, ActionHandler> actionHandlerMap(Map<String, ActionHandler> handlers) {
        Map<DrawEventAction, ActionHandler> actionHandlerMap = new HashMap<>();
        Pattern pattern = Pattern.compile(NamePattern);

        for(var entry: handlers.entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());
            if (!matcher.find()) throw new RuntimeException("no matched action");

            String key = matcher.group(1);
            ActionHandler actionHandler = entry.getValue();

            actionHandlerMap.put(DrawEventAction.valueOf(key), actionHandler);
        }
        // 런타임에 변경 못하게 제한
        return Collections.unmodifiableMap(actionHandlerMap);
    }
}
