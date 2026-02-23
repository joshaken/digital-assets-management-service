package co.assets.manage.config.exception;

import co.assets.manage.enums.api.APIEnum;
import co.assets.manage.enums.api.IResultMsg;

//下流サービスの例外
public class ForwardServiceException extends BizException {
    private Integer code;

    private String message;

    public ForwardServiceException(String msg) {
        super(new IResultMsg() {
            @Override
            public Integer getCode() {
                return APIEnum.FORWARD_ERROR.getCode();
            }

            @Override
            public String getMessage() {
                return msg;
            }
        });
    }

    public ForwardServiceException(IResultMsg error, String message) {
        super(error);
        this.message = message;
    }


}
