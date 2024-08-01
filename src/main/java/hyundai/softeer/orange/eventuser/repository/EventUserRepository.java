package hyundai.softeer.orange.eventuser.repository;

import hyundai.softeer.orange.eventuser.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    Optional<EventUser> findByUserNameAndPhoneNumber(String userName, String phoneNumber);

    Optional<EventUser> findByUserId(String userId);
}
