package co.assets.manage.dto;


import co.assets.manage.config.exception.AbstractException;
import co.assets.manage.config.exception.ForwardServiceException;
import co.assets.manage.enums.api.IResultMsg;
import co.assets.manage.enums.api.APIEnum;
import co.assets.manage.utils.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Slf4j
@Getter
@Setter
@ToString
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4855871324022065224L;

    //    @Schema(description = "返回状态 IResultMsg", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    //    @Schema(description = "返回状态对应的消息", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    //    @Schema(description = "返回结果中的数据体")
    private T data;

    //    @Schema(description = "异常的堆栈信息")
    private Map errorInfo;

    //    @Schema(description = "返回的错误消息")
    private String errorMessage;

    /**
     * 检查返回状态是否为成功状态
     */
    public boolean isSuccess() {
        return status >= 0;
    }

    /**
     * 静态方法检查返回结果是否为成功状态
     *
     * @param result
     * @return
     */
    public static boolean isSuccess(Result result) {
        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }

    public Result() {
    }

    Result(T data) {
        this.status = APIEnum.SUCCESS.getCode();
        this.message = APIEnum.SUCCESS.getMessage();
        this.data = data;
    }

    Result(T data, Integer status, String message) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    Result(IResultMsg result) {
        this.status = result.getCode();
        this.message = result.getMessage();
        this.data = null;
    }

    Result(IResultMsg result, String message) {
        this.status = result.getCode();
        this.message = message;
        this.data = null;
    }

    Result(IResultMsg result, T data) {
        this.status = result.getCode();
        this.message = result.getMessage();
        this.data = data;
    }

    Result(Integer status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    /**
     * 返回操作成功对应的API响应
     *
     * @return API响应对象
     */
    public static <T> Result<T> ok() {
        return Result.getCustomResponse(APIEnum.SUCCESS);
    }

    /**
     * 返回操作成功对应的API响应
     *
     * @return API响应对象
     */
    public static <T> Result<T> ok(T data) {
        assert !(data instanceof IResultMsg);
        return Result.getCustomResponse(APIEnum.SUCCESS, data);
    }

    /**
     * 返回自定义的任意数据
     *
     * @param status 状态
     * @param data   数据
     * @return API响应对象
     */
    public static <T> Result<T> ok(IResultMsg status, T data) {
        Result<T> result = getCustomResponse(status);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        return Result.getCustomResponse(APIEnum.FAILED);
    }


    public static <T> Result<T> paramError() {
        return Result.getCustomResponse(APIEnum.FAILED);
    }

    /**
     * 返回操作成功对应的API响应
     *
     * @return API响应对象
     */
    public static <T> Result<T> error(T data) {
        assert !(data instanceof IResultMsg);
        return Result.getCustomResponse(APIEnum.FAILED, data);
    }


    /**
     * 返回结果码对应的API响应
     *
     * @param status 状态
     * @return API响应对象
     */
    public static <T> Result<T> getCustomResponse(IResultMsg status) {
        Result<T> result = new Result<>();
        result.setStatus(status.getCode());
        result.setMessage(status.getMessage());
        return result;
    }

    public static <T> Result<T> getCustomResponse(ForwardServiceException e) {
        Result<T> result = new Result<>();
        result.setStatus(APIEnum.FOR_WORDING_ERROR.getCode());
        result.setMessage(e.getMessage());
        return result;
    }

    public static <T> Result<T> getErrorResponse(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setStatus(code);
        result.setMessage(msg);
        return result;
    }

    /**
     * 返回自定义的任意数据
     *
     * @param status 状态
     * @param data   数据
     * @return API响应对象
     */
    public static <T> Result<T> getCustomResponse(IResultMsg status, T data) {
        Result<T> result = getCustomResponse(status);
        result.setData(data);
        return result;
    }

    /**
     * 返回参数异常数据
     *
     * @param status
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> getErrorResponse(IResultMsg status, T data) {
        Result<T> result = getCustomResponse(status);
        result.setMessage(result.getMessage() + data);
        return result;
    }

    public static <T> Result<T> getErrorResponse(Throwable e) {
        Result<T> result = new Result<>();
        if (e instanceof AbstractException && ((AbstractException) e).getMsg() != null) {
            AbstractException exception = (AbstractException) e;
            result.setStatus(exception.getMsg().getCode());
            result.setMessage(e.getMessage());
        } else {
            result.setStatus(APIEnum.FAILED.getCode());
            result.setMessage(e.getMessage());
        }
        result.setErrorInfo(ExceptionUtils.getExceptionMainInfo(e));
        result.setErrorMessage(e.getMessage());
        return result;
    }

    /**
     * 在失败的情况下拷贝错误消息等
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> Result<T> convertResult(Result result) {
        if (result == null) {
            return Result.getCustomResponse(APIEnum.FAILED);
        }
        Result<T> r = new Result<>();
        r.setErrorMessage(result.errorMessage);
        r.setStatus(result.status);
        r.setErrorInfo(result.errorInfo);
        r.setMessage(result.message);
        return r;
    }
}
