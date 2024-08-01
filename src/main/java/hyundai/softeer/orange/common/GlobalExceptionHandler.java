package hyundai.softeer.orange.common;

import hyundai.softeer.orange.admin.exception.AdminException;
import hyundai.softeer.orange.comment.exception.CommentException;
import hyundai.softeer.orange.common.exception.InternalServerException;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.eventuser.exception.EventUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // 요청의 유효성 검사 실패 시
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request로 응답 반환
    public ResponseEntity<Map<String, String>> handleInValidRequestException(MethodArgumentNotValidException e) {
        // 에러가 발생한 객체 내 필드와 대응하는 에러 메시지를 map에 저장하여 반환
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({CommentException.class, AdminException.class})
    public ResponseEntity<ErrorResponse> handleCommentException(BaseException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler(EventUserException.class)
    public ResponseEntity<ErrorResponse> handleEventUserException(EventUserException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler(FcfsEventException.class)
    public ResponseEntity<ErrorResponse> handleFcfsEventException(FcfsEventException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }
}
