package roomescape.exception.business;

import roomescape.exception.ErrorCode;

public class ForbiddenStoreAccessException extends BusinessException {

    public ForbiddenStoreAccessException() {
        super(ErrorCode.FORBIDDEN_STORE_ACCESS);
    }
}