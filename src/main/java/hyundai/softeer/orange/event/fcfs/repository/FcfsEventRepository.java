package hyundai.softeer.orange.event.fcfs.repository;

import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FcfsEventRepository extends JpaRepository<FcfsEvent, Long> {

    List<FcfsEvent> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
