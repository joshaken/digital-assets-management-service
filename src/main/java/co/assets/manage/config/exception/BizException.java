package co.assets.manage.config.exception;

import co.assets.manage.enums.api.APIEnum;
import co.assets.manage.enums.IResultMsg;

import java.io.Serial;

/**
 * 业务异常
 */
//@Schema(description = "业务的异常模型")
public class BizException extends AbstractException {
    @Serial
    private static final long serialVersionUID = -644174310560740232L;

    public BizException(IResultMsg error) {
        super(error);
    }

    public BizException(String error) {

        super(new IResultMsg() {

            @Serial
            private static final long serialVersionUID = -7026523857294062402L;

            @Override
            public Integer getCode() {
                return APIEnum.BIZ_ERROR.getCode();
            }

            @Override
            public String getMessage() {
                return error;
            }
        });
    }

    public BizException(IResultMsg error, Throwable cause) {
        super(error, cause);
    }

    public BizException(Throwable cause) {
        super(APIEnum.BIZ_ERROR, cause);
    }
}