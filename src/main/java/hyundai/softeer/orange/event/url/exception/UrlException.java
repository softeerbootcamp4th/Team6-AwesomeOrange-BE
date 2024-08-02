package hyundai.softeer.orange.event.url.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class UrlException extends BaseException {
    public UrlException(ErrorCode errorCode) {
        super(errorCode);
    }
}
