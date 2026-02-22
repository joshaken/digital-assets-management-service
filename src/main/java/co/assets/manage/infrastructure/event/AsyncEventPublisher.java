package co.assets.manage.infrastructure.event;

import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.infrastructure.ai.AssetAddTagPublisher;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "config.tag.add", havingValue = "event", matchIfMissing = true)
public class AsyncEventPublisher implements AssetAddTagPublisher {


    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void sendCreateAssetEvent(AssetTagEvent assetTagEvent) {

        eventPublisher.publishEvent(assetTagEvent);
    }
}
