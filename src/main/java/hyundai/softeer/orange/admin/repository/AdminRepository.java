package hyundai.softeer.orange.admin.repository;

import hyundai.softeer.orange.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findFirstByUsername(String username);
    boolean existsByUsername(String username);
}
