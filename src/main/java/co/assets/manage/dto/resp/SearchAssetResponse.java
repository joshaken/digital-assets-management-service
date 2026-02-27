package co.assets.manage.dto.resp;

/**
 * Assetを検索するレスポンスクラス（不要なフィールドを除外）
 *
 * @param id assetId
 * @param title    Asset title
 * @param filePath Asset filePath
 */
public record SearchAssetResponse(
        String id,
        String title,
        String filePath
) {
}
