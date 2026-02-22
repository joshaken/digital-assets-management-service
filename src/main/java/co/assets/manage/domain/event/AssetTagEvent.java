package co.assets.manage.domain.event;

/**
 * Asset保存後に下流へメッセージを送信し、下流はそのメッセージ内容を基に外部AI
 * APIを呼び出してAssetのタグをマッチング
 *
 * @param assetId  assetId
 * @param filePath asset file path
 */
public record AssetTagEvent(Long assetId, String filePath) {
}
