package hyundai.softeer.orange.event.fcfs.repository;

import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcfsEventWinningInfoRepository extends JpaRepository<FcfsEventWinningInfo, Long> {

    // Fetch Join으로 eventUser 정보까지 한번에 가져와서 N+1 문제 방지
    @Query("select f from FcfsEventWinningInfo f join fetch f.eventUser where f.fcfsEvent.id = :eventSequence")
    List<FcfsEventWinningInfo> findByFcfsEventId(Long eventSequence);
}
