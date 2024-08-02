package hyundai.softeer.orange.event.url.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "url")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;

    private String shortUrl;

    public static Url of(String originalUrl, String shortUrl) {
        Url url = new Url();
        url.originalUrl = originalUrl;
        url.shortUrl = shortUrl;
        return url;
    }
}
