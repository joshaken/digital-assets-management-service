package co.assets.manage.dto.req;

public record QueryAssetRequest(
        String tag,
        String status,
        Integer page,
        Integer size
) {
}
