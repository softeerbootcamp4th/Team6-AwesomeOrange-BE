package hyundai.softeer.orange.comment.repository;

import hyundai.softeer.orange.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
