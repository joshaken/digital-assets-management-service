package co.assets.manage.dto;


import co.assets.manage.config.exception.AbstractException;
import co.assets.manage.enums.api.APIEnum;
import co.assets.manage.enums.api.IResultMsg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4855871324022065224L;

    //リクエスト用のカスタムレスポンスコード
    private Integer status;

    //ステータスに対応するメッセージ
    private String message;

    //レスポンスのデータ本体
    private T data;

    /**
     * レスポンスのステータスが成功かどうかをチェック
     */
    public boolean isSuccess() {
        return status >= 0;
    }


    /**
     * 操作成功に対応するAPIレスポンスを返す
     */
    public static <T> Result<T> ok() {
        return Result.getCustomResponse(APIEnum.SUCCESS);
    }

    /**
     * 操作成功に対応するAPIレスポンスを返す
     * オブジェクト本体を含む
     */
    public static <T> Result<T> ok(T data) {
        assert !(data instanceof IResultMsg);
        return Result.getCustomResponse(APIEnum.SUCCESS, data);
    }


    /**
     * IResultMsg に対応するAPIレスポンスを返す
     *
     * @param status 結果コード
     */
    public static <T> Result<T> getCustomResponse(IResultMsg status) {
        Result<T> result = new Result<>();
        result.setStatus(status.getCode());
        result.setMessage(status.getMessage());
        return result;
    }


    /**
     * カスタムの任意データを返す
     *
     * @param status 結果コード
     * @param data   data
     */
    public static <T> Result<T> getCustomResponse(IResultMsg status, T data) {
        Result<T> result = getCustomResponse(status);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        return Result.getCustomResponse(APIEnum.FAILED);
    }

    public static <T> Result<T> error(T data) {
        return Result.getCustomResponse(APIEnum.FAILED, data);
    }

    public static <T> Result<T> getErrorResponse(Throwable e) {
        Result<T> result = new Result<>();
        if (e instanceof AbstractException exception && ((AbstractException) e).getMsg() != null) {
            result.setStatus(exception.getMsg().getCode());
            result.setMessage(e.getMessage());
        } else {
            result.setStatus(APIEnum.FAILED.getCode());
            result.setMessage(e.getMessage());
        }
        return result;
    }


    public static <T> Result<T> getErrorResponse(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setStatus(code);
        result.setMessage(msg);
        return result;
    }
}
