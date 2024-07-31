package hyundai.softeer.orange.comment;

import hyundai.softeer.orange.comment.dto.CreateCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentsDto;
import hyundai.softeer.orange.comment.entity.Comment;
import hyundai.softeer.orange.comment.exception.CommentException;
import hyundai.softeer.orange.comment.repository.CommentRepository;
import hyundai.softeer.orange.comment.service.CommentService;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EventFrameRepository eventFrameRepository;

    @Mock
    private EventUserRepository eventUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    Long commentId = 1L;
    CreateCommentDto createCommentDto = CreateCommentDto.builder()
            .eventUserId(1L)
            .eventFrameId(1L)
            .content("test")
            .build();
    EventUser eventUser = EventUser.of("test", "01012345678", null, "uuid");
    EventFrame eventFrame = EventFrame.of("eventFrame");

    @DisplayName("getComments: 무작위 긍정 기대평 목록을 조회한다.")
    @Test
    void getCommentsTest() {
        // given
        given(commentRepository.findRandomPositiveComments(ConstantUtil.COMMENTS_SIZE))
                .willReturn(List.of(Comment.of("test", eventFrame, eventUser, true)));

        // when
        ResponseCommentsDto dto = commentService.getComments();

        // then
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getContent()).isEqualTo("test");
        verify(commentRepository, times(1)).findRandomPositiveComments(anyInt());
    }

    @DisplayName("getComments: 무작위 긍정 기대평 목록이 없는 경우 빈 목록을 반환한다.")
    @Test
    void getCommentsTestEmpty() {
        // given
        given(commentRepository.findRandomPositiveComments(ConstantUtil.COMMENTS_SIZE)).willReturn(List.of());

        // when
        ResponseCommentsDto dto = commentService.getComments();

        // then
        assertThat(dto.getComments()).isEmpty();
        verify(commentRepository, times(1)).findRandomPositiveComments(anyInt());
    }

    @DisplayName("createComment: 신규 기대평을 작성한다.")
    @Test
    void createCommentTest() {
        // given
        given(commentRepository.existsByCreatedDateAndEventUser(createCommentDto.getEventUserId())).willReturn(false);
        given(eventFrameRepository.findById(createCommentDto.getEventFrameId())).willReturn(Optional.of(EventFrame.of("eventFrame")));
        given(eventUserRepository.findById(createCommentDto.getEventUserId())).willReturn(Optional.ofNullable(eventUser));
        given(commentRepository.save(any())).willReturn(Comment.of("test", eventFrame, eventUser, true));

        // when
        commentService.createComment(createCommentDto, true);

        // then
        verify(commentRepository, times(1)).save(any());
        verify(commentRepository, times(1)).existsByCreatedDateAndEventUser(createCommentDto.getEventUserId());
        verify(eventFrameRepository, times(1)).findById(createCommentDto.getEventFrameId());
        verify(eventUserRepository, times(1)).findById(createCommentDto.getEventUserId());
        verify(commentRepository, times(1)).save(any());
    }

    @DisplayName("createComment: 하루에 여러 번의 기대평을 작성하려 할 때 예외가 발생한다.")
    @Test
    void createCommentAlreadyExistsTest() {
        // given
        given(commentRepository.existsByCreatedDateAndEventUser(createCommentDto.getEventUserId())).willReturn(true);

        // when
        assertThatThrownBy(() -> commentService.createComment(createCommentDto, true))
                .isInstanceOf(CommentException.class)
                .hasMessage(ErrorCode.COMMENT_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("createComment: EventFrame을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void createCommentFrameNotFoundTest() {
        // given
        given(commentRepository.existsByCreatedDateAndEventUser(createCommentDto.getEventUserId())).willReturn(false);
        given(eventFrameRepository.findById(createCommentDto.getEventFrameId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(createCommentDto, true))
                .isInstanceOf(CommentException.class)
                .hasMessage(ErrorCode.EVENT_FRAME_NOT_FOUND.getMessage());
    }

    @DisplayName("createComment: EventUser을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void createCommentUserNotFoundTest() {
        // given
        given(commentRepository.existsByCreatedDateAndEventUser(createCommentDto.getEventUserId())).willReturn(false);
        given(eventFrameRepository.findById(createCommentDto.getEventFrameId())).willReturn(Optional.of(EventFrame.of("eventFrame")));
        given(eventUserRepository.findById(createCommentDto.getEventUserId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(createCommentDto, true))
                .isInstanceOf(CommentException.class)
                .hasMessage(ErrorCode.EVENT_USER_NOT_FOUND.getMessage());
    }

    @DisplayName("deleteComment: commentId로 기대평을 찾아 삭제한다.")
    @Test
    void deleteCommentTest() {
        // given
        given(commentRepository.existsById(commentId)).willReturn(true);
        doNothing().when(commentRepository).deleteById(commentId);

        // when
        Long deletedCommentId = commentService.deleteComment(commentId);

        // then
        assertThat(deletedCommentId).isEqualTo(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @DisplayName("deleteComment: 기대평을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void deleteCommentNotFoundTest() {
        // given
        given(commentRepository.existsById(commentId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(commentId))
                .isInstanceOf(CommentException.class)
                .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getMessage());
    }
}
