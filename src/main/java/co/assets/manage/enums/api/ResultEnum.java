package co.assets.manage.enums.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnum implements IResultMsg {

    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 操作失败
     */
    ERROR(9999, "error"),

    /**
     * token校验失败
     */
    TOKEN_FAIL(9999, "token校验失败"),

    /**
     * 用户数据解析错误
     */
    USER_NOT_FOUND(9999, "用户信息错误"),

    /**
     * 操作失败
     */
    FAIL(9999, "server is error"),

    /**
     * 0001 参数不全，必填字段为空，参数格式不正确
     */
    INTERFACE_PARAM(1, "参数格式不正确"),

    /**
     * 0004 数据异常，查询失败
     */
    DATA_SELECT(4, "数据异常，查询失败"),

    /**
     * 0005 数据插入失败
     */
    DATA_INSERT(5, "数据插入失败"),
    /**
     * 0006 数据更新失败
     */
    DATA_UPDATE(6, "数据更新失败"),
    /**
     * 0007 数据删除失败
     */
    DATA_DELETE(7, "数据删除失败"),

    ;


    private final Integer code;

    private final String message;

}
