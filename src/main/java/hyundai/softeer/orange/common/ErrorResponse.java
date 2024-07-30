package hyundai.softeer.orange.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ErrorResponse {

    private String error;
    private String errorMessage;

    public static ErrorResponse from(ErrorCode errorCode) {
        ErrorResponse response = new ErrorResponse();
        response.error = errorCode.name();
        response.errorMessage = errorCode.getMessage();
        return response;
    }
}
