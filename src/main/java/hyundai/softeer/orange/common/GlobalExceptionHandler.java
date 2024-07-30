package hyundai.softeer.orange.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import hyundai.softeer.orange.comment.exception.CommentException;
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

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessException(JsonProcessingException e) {
        return ResponseEntity.status(ErrorCode.INVALID_JSON.getHttpStatus()).body(ErrorCode.INVALID_JSON.getMessage());
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<String> handleCommentException(CommentException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getMessage());
    }
}
