package hyundai.softeer.orange.event.url.repository;

import hyundai.softeer.orange.event.url.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByShortUrl(String shortUrl);
    Optional<Url> findByShortUrl(String shortUrl);
}
