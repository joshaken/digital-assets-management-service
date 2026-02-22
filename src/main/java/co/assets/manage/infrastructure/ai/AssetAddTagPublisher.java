package co.assets.manage.infrastructure.ai;

import co.assets.manage.domain.event.AssetTagEvent;

public interface AssetAddTagPublisher {

    void sendCreateAssetEvent(AssetTagEvent assetTagEvent);
}
