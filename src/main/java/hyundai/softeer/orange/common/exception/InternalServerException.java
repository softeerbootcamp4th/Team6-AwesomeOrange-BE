package hyundai.softeer.orange.common.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class InternalServerException extends BaseException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InternalServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
