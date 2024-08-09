package hyundai.softeer.orange.common;

import hyundai.softeer.orange.admin.exception.AdminException;
import hyundai.softeer.orange.comment.exception.CommentException;

import hyundai.softeer.orange.common.exception.InternalServerException;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.url.exception.UrlException;
import hyundai.softeer.orange.eventuser.exception.EventUserException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;

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

    // TODO: messages.properties에 예외 메시지 커스터마이징할 수 있게 방법 찾아보기
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleInValidRequestException(MethodArgumentTypeMismatchException e) {
        String code = e.getErrorCode();
        String fieldName = e.getName();
        Locale locale = LocaleContextHolder.getLocale(); // 현재 스레드의 로케일 정보를 가져온다.
        String errorMessage = messageSource.getMessage(code, null, locale); // 국제화 된 메시지를 가져온다.

        Map<String, String> error = new HashMap<>();
        error.put(fieldName, errorMessage);
        return error;
    }

    @ExceptionHandler({CommentException.class, AdminException.class, EventUserException.class, FcfsEventException.class, UrlException.class, InternalServerException.class})
    public ResponseEntity<ErrorResponse> handleException(BaseException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorResponse> handleAllBaseException(BaseException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }
}
