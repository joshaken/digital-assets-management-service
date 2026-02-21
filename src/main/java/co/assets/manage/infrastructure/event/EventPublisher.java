package co.assets.manage.infrastructure.event;

import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.domain.model.AssetDO;

public interface EventPublisher {

    void sendCreateAssetEvent(AssetTagEvent assetTagEvent);
}
