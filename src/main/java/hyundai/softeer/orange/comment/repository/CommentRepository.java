package hyundai.softeer.orange.comment.repository;

import hyundai.softeer.orange.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // DB 상에서 무작위로 추출된 n개의 긍정 기대평 목록을 조회
    @Query(value = "SELECT * FROM comment WHERE is_positive = true ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<Comment> findRandomPositiveComments(@Param("n") int n);

    // 오늘 날짜 기준으로 이미 유저의 기대평이 등록되어 있는지 확인
    @Query(value = "SELECT COUNT(*) FROM comment WHERE event_user_id = :eventUserId AND DATE(createdAt) = CURDATE()", nativeQuery = true)
    boolean existsByCreatedDateAndEventUser(@Param("eventUserId") Long eventUserId);
}
