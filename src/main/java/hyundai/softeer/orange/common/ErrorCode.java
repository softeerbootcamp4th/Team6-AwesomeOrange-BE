package hyundai.softeer.orange.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {

    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, false, "잘못된 요청입니다."),
    INVALID_COMMENT(HttpStatus.BAD_REQUEST, false, "부정적인 표현을 사용하였습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, false, "잘못된 JSON 형식입니다."),
    INVALID_URL(HttpStatus.BAD_REQUEST, false, "유효하지 않은 URL입니다."),
    INVALID_EVENT_TIME(HttpStatus.BAD_REQUEST, false, "이벤트 시간이 아닙니다."),
    INVALID_EVENT_TYPE(HttpStatus.BAD_REQUEST, false, "이벤트 타입이 지원되지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, false, "인증되지 않은 사용자입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, false, "아이디 또는 비밀번호가 일치하지 않습니다"),
    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, false, "인증번호가 일치하지 않습니다."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, false, "세션이 만료되었습니다."),
    AUTH_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, false, "인증번호가 만료되었거나 존재하지 않습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, false, "권한이 없습니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, false, "사용자를 찾을 수 없습니다."),
    SHORT_URL_NOT_FOUND(HttpStatus.NOT_FOUND, false, "단축 URL을 찾을 수 없습니다."),
    FCFS_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "선착순 이벤트를 찾을 수 없습니다."),
    DRAW_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "추첨 이벤트를 찾을 수 없습니다."),
    EVENT_FRAME_NOT_FOUND(HttpStatus.NOT_FOUND, false, "이벤트 프레임을 찾을 수 없습니다."),
    EVENT_USER_NOT_FOUND(HttpStatus.NOT_FOUND, false, "이벤트 사용자를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "기대평을 찾을 수 없습니다."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "이벤트를 찾을 수 없습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, false, "허용되지 않은 메서드입니다."),

    // 409 Conflict
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, false, "이미 존재하는 사용자입니다."),
    SHORT_URL_ALREADY_EXISTS(HttpStatus.CONFLICT, false, "이미 존재하는 URL입니다."),
    COMMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, false, "이미 등록된 기대평입니다."),
    ADMIN_USER_ALREADY_EXISTS(HttpStatus.CONFLICT, false, "이미 존재하는 관리자입니다."),
    ALREADY_WINNER(HttpStatus.CONFLICT, false, "이미 당첨된 사용자입니다."),
    ALREADY_PARTICIPATED(HttpStatus.CONFLICT, false, "이미 참여한 사용자입니다."),
    PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, false, "이미 존재하는 전화번호입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "서버에 오류가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final Boolean success;
    private final String message;
}
