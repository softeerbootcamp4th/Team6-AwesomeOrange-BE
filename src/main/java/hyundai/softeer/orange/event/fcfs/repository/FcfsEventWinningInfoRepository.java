package hyundai.softeer.orange.event.fcfs.repository;

import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcfsEventWinningInfoRepository extends JpaRepository<FcfsEventWinningInfo, Long> {
}
