package co.assets.manage.infrastructure.mq;

import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.infrastructure.ai.AssetAddTagPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(value = "config.tag.add", havingValue = "mq")
public class AssetTagMqPublisher implements AssetAddTagPublisher {

    @Override
    public void sendCreateAssetEvent(AssetTagEvent assetTagEvent) {

    }
}
