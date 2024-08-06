package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrawEventRepository extends JpaRepository<DrawEvent, Long> { }
