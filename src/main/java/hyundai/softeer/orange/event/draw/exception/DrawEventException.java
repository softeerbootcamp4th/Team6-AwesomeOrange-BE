package hyundai.softeer.orange.event.draw.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class DrawEventException extends BaseException {
    public DrawEventException(ErrorCode errorCode) {
        super(errorCode);
    }
}
