package hyundai.softeer.orange.eventuser.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class EventUserException extends BaseException {

    public EventUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
