package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawEventRepository extends JpaRepository<DrawEvent, Long> {
}
