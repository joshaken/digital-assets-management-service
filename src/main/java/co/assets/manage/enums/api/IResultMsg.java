package co.assets.manage.enums.api;

import java.io.Serializable;

/**
 * 汎用結果情報
 **/
public interface IResultMsg extends Serializable {

    Integer getCode();

    String getMessage();


}
