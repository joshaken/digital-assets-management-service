package co.assets.manage.config.exception;

import co.assets.manage.enums.api.APIEnum;
import co.assets.manage.enums.api.IResultMsg;
import lombok.Getter;

import java.io.Serial;

@Getter
public abstract class AbstractException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1813767369790744025L;

    //エラー情報を含むオブジェクト
    private final IResultMsg msg;

    public AbstractException(IResultMsg error) {
        super(error.getMessage());
        msg = error;
    }

    public AbstractException(IResultMsg error, Throwable cause) {
        super(error.getMessage(), cause);
        msg = error;
    }


    public AbstractException(Throwable cause) {
        super(cause);
        msg = APIEnum.FAILED;
    }
}
