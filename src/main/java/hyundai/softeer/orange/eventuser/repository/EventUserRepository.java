package hyundai.softeer.orange.eventuser.repository;

import hyundai.softeer.orange.eventuser.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    Optional<EventUser> findByUserNameAndPhoneNumber(String userName, String phoneNumber);

    Optional<EventUser> findByUserId(String userId);

    @Query("select eu from EventUser eu where eu.userId in :userIds")
    List<EventUser> findAllByUserId(@Param("userIds") List<String> userIds);

    boolean existsByPhoneNumber(String phoneNumber);
}
