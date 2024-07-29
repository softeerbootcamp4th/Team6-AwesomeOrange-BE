package hyundai.softeer.orange.eventuser.repository;

import hyundai.softeer.orange.eventuser.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventUserRepository extends JpaRepository<EventUser, Long> {
}
