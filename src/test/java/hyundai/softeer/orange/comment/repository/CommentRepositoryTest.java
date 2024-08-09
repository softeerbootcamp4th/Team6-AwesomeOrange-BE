package hyundai.softeer.orange.comment.repository;

import hyundai.softeer.orange.comment.dto.WriteCommentCountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 매 테스트마다 초기화하는 코드 찾아봐야 할듯?
@Sql(value = "classpath:sql/CommentRepositoryTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DataJpaTest(showSql = false)
@TestPropertySource(locations = "classpath:application-test.yml")
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;


    @DisplayName("존재하는 대상 이벤트에 대해 작성된 댓글이 있다면 유저 별로 개수를 구해 반환")
    @Test
    void getCountOfCommentPerUserIfCommentExist() {
        List<WriteCommentCountDto> counts = commentRepository.countPerEventUserByEventId(1L);

        assertThat(counts).hasSize(3);
        assertThat(counts.get(0).getCount()).isEqualTo(3);
        assertThat(counts.get(1).getCount()).isEqualTo(6);
        assertThat(counts.get(2).getCount()).isEqualTo(2);
    }

    @DisplayName("존재하지 않는 대상 이벤트는 빈 배열 반환")
    @Test
    void getCountOfCommentPerUserIfEventNotExist() {
        List<WriteCommentCountDto> counts = commentRepository.countPerEventUserByEventId(3L);
        assertThat(counts).hasSize(0);
    }

    @DisplayName("존재해도 댓글 없으면 빈 배열 반환")
    @Test
    void getCountOfCommentPerUserIfEventExistButNoComment() {
        List<WriteCommentCountDto> counts = commentRepository.countPerEventUserByEventId(2L);
        assertThat(counts).hasSize(0);
    }
}