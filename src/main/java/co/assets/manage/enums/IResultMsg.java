package co.assets.manage.enums;

import java.io.Serializable;

/**
 * 结果信息
 **/
public interface IResultMsg extends Serializable {

    /**
     * 获取错误代码
     */
    Integer getCode();

    /**
     * 获取错误消息
     */
    String getMessage();


}
