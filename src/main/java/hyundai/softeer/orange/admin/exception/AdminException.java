package hyundai.softeer.orange.admin.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class AdminException extends BaseException {
    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }
}
