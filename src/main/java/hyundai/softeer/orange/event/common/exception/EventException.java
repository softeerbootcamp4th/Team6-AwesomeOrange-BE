package hyundai.softeer.orange.event.common.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class EventException extends BaseException {
    public EventException(ErrorCode errorCode) {
        super(errorCode);
    }
}
