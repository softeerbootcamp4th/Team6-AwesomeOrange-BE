package hyundai.softeer.orange.event.fcfs.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class FcfsEventException extends BaseException {
    public FcfsEventException(ErrorCode errorCode) {
        super(errorCode);
    }
}
