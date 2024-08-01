package hyundai.softeer.orange.event.url.entity;

import hyundai.softeer.orange.eventuser.entity.EventUser;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_user_id")
    private EventUser eventUser;

    public static Url of(String originalUrl, String shortUrl, EventUser eventUser) {
        Url url = new Url();
        url.originalUrl = originalUrl;
        url.shortUrl = shortUrl;
        url.eventUser = eventUser;
        return url;
    }
}
