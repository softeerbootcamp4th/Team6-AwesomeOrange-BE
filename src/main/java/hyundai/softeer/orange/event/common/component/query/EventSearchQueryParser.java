package hyundai.softeer.orange.event.common.component.query;

import java.util.HashMap;
import java.util.Map;

public class EventSearchQueryParser {
    public static Map<String, String> parse(String searchQuery) {
        Map<String, String> map = new HashMap<>();
        // searchQuery가 null이면 빈 map 반환
        if (searchQuery == null) return map;

        String[] queries = searchQuery.split(",");

        for (String query : queries) {
            String[] pair = query.split(":");
            if(pair.length <= 0 || pair.length > 2) continue; // 값이 없거나 넘치면 무시.
            String key = pair[0].trim();
            String value;
            if(pair.length > 1) value = pair[1].trim();
            else value = "";

            map.put(key, value);
        }
        return map;
    }
}
