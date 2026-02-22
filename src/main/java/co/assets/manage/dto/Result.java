package co.assets.manage.dto;


import co.assets.manage.config.exception.AbstractException;
import co.assets.manage.config.exception.ForwardServiceException;
import co.assets.manage.enums.api.IResultMsg;
import co.assets.manage.enums.api.APIEnum;
import co.assets.manage.utils.ExceptionUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
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

}
