package hyundai.softeer.orange.event.fcfs.repository;

import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FcfsEventRepository extends JpaRepository<FcfsEvent, Long> {

    List<FcfsEvent> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from FcfsEvent e left join fetch e.infos where e.id = :id")
    Optional<FcfsEvent> findByIdWithLock(Long id);
}
