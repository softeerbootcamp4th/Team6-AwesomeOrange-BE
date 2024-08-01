package hyundai.softeer.orange.event.url.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLConnection;

@Component
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlTypeValidation {
    public static boolean valid(String url) {
        try {
            URL connectionUrl = new URL(url);
            URLConnection conn = connectionUrl.openConnection();
            conn.connect();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
