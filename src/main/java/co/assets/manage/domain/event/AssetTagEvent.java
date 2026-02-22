package co.assets.manage.domain.event;

public record AssetTagEvent(
        Long assetId
        , String filePath
) {
}
