package hyundai.softeer.orange.comment.repository;

import hyundai.softeer.orange.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM comment WHERE is_positive = true ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<Comment> findRandomPositiveComments(@Param("n") int n);
}
