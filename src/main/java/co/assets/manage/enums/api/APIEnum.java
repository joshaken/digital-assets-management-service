package co.assets.manage.enums.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * APIの汎用ステータスとメッセージの列挙型
 */
@Getter
@AllArgsConstructor
public enum APIEnum implements IResultMsg {
    SUCCESS(0, "成功"),
    FAILED(-1, "失敗"),
    PARAM_ERROR(-301, "パラメータエラー"),
    DATA_ERROR(-302, "データが存在しません"),
    SERVER_ERROR(-500, "サーバーエラー"),
    NOT_FOUND(-404, "リソースが見つかりません"),
    FORWARD_ERROR(-501, "下流サービスアクセス異常"),
    PREVIOUS_ERROR(-502, "上流サービスの値渡し異常"),
    SECURITY_ERROR(-503, "セキュリティエラー"),
    NOT_LOGIN_ERROR(-10101, "ログインしてください"),
    UNAUTHORIZED(-401, "認証に失敗しました"),
    FORBIDDEN(-403, "アクセス禁止"),
    NO_PERMISSION(-10104, "権限がありません"),
    BIZ_ERROR(-10200, "業務エラー"),
    DECODE_ERROR(-150, "レスポンス解析エラー"),
    DUPLICATED_INSERT(-10201, "データは既に存在しているため、重複して新規追加できません。")

    ;

    private final Integer code;

    private final String message;
}
