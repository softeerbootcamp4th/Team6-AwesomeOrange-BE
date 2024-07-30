package hyundai.softeer.orange.comment.exception;

import hyundai.softeer.orange.common.BaseException;
import hyundai.softeer.orange.common.ErrorCode;

public class CommentException extends BaseException {

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
