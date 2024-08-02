package hyundai.softeer.orange.event.url.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlTypeValidation {

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
