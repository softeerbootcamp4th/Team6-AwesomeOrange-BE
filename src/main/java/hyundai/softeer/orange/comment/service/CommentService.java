package hyundai.softeer.orange.comment.service;

import hyundai.softeer.orange.comment.dto.CreateCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentsDto;
import hyundai.softeer.orange.comment.entity.Comment;
import hyundai.softeer.orange.comment.exception.CommentException;
import hyundai.softeer.orange.comment.repository.CommentRepository;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventFrameRepository eventFrameRepository;
    private final EventUserRepository eventUserRepository;

    // 주기적으로 무작위 추출되는 긍정 기대평 목록을 조회한다.
    @Transactional(readOnly = true)
    @Cacheable(value = "comments", key = ConstantUtil.COMMENTS_KEY)
    public ResponseCommentsDto getComments() {
        List<ResponseCommentDto> comments = commentRepository.findRandomPositiveComments(ConstantUtil.COMMENTS_SIZE)
                .stream()
                .map(ResponseCommentDto::from)
                .toList();
        return new ResponseCommentsDto(comments);
    }

    // 신규 기대평을 등록한다.
    @Transactional
    public Boolean createComment(String userId, CreateCommentDto dto, Boolean isPositive) {
        EventUser eventUser = eventUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CommentException(ErrorCode.EVENT_USER_NOT_FOUND));
        EventFrame eventFrame = eventFrameRepository.findById(dto.getEventFrameId())
                .orElseThrow(() -> new CommentException(ErrorCode.EVENT_FRAME_NOT_FOUND));

        // 하루에 여러 번의 기대평을 작성하려 할 때 예외처리
        if(commentRepository.existsByCreatedDateAndEventUser(eventUser.getId())) {
            throw new CommentException(ErrorCode.COMMENT_ALREADY_EXISTS);
        }

        // TODO: 점수정책와 연계하여 기대평 등록 시 점수를 부여 추가해야함
        Comment comment = Comment.of(dto.getContent(), eventFrame, eventUser, isPositive);
        commentRepository.save(comment);
        return true;
    }

    // 오늘 이 유저가 기대평을 작성할 수 있는지 여부를 조회한다.
    @Transactional(readOnly = true)
    public Boolean isCommentable(String userId) {
        EventUser eventUser = eventUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CommentException(ErrorCode.EVENT_USER_NOT_FOUND));
        return !commentRepository.existsByCreatedDateAndEventUser(eventUser.getId());
    }

    // 기대평을 삭제한다. 이 동작을 실행하는 주체가 어드민임이 반드시 검증되어야 한다.
    @Transactional
    public Long deleteComment(Long commentId) {
        if(!commentRepository.existsById(commentId)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_FOUND);
        }

        commentRepository.deleteById(commentId);
        return commentId;
    }
}
