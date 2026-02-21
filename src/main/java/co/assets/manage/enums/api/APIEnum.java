package co.assets.manage.enums.api;

import co.assets.manage.enums.IResultMsg;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum APIEnum implements IResultMsg {
    // 通用码
    SUCCESS(0, "成功"),
    FAILED(-1, "失败"),
    PARAM_ERROR(-301, "参数错误"),
    DATA_ERROR(-302, "数据不存在"),
    SERVER_ERROR(-500, "服务器错误"),
    NOT_FOUND(-404, "地址不存在"),
    FOR_WORDING_ERROR(-501, "下游服务访问异常"),
    PREVIOUS_ERROR(-502, "上游服务传值异常"),
    SECURITY_ERROR(-503, "安全性异常"),
    NOT_LOGIN_ERROR(-10101, "请您登录"),
    UNAUTHORIZED(-401, "鉴权不通过"),
    FORBIDDEN(-403, "禁止访问"),
    TIME_OUT_ERROR(-10103, "请求时间超时"),
    NO_PERMISSION(-10104, "没有权限"),
    BIZ_ERROR(-10200, "业务异常"),
    DECODE_ERROR(-150, "异常响应"),
    SMS_POLICY_APPLY_ERROR(-10201, "短信能力策略应用业务异常"),

    ;

    private final Integer code;

    private final String message;
}
