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

    private String shortUrl;

    private String longUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_user_id")
    private EventUser eventUser;

    public static Url of(String longUrl, String url, EventUser eventUser) {
        Url shortUrl = new Url();
        shortUrl.longUrl = longUrl;
        shortUrl.shortUrl = url;
        shortUrl.eventUser = eventUser;
        return shortUrl;
    }
}
