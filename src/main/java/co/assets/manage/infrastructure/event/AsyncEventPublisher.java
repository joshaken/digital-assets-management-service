package co.assets.manage.infrastructure.event;

import co.assets.manage.domain.event.AssetTagEvent;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AsyncEventPublisher implements EventPublisher {


    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void sendCreateAssetEvent(AssetTagEvent assetTagEvent) {
        eventPublisher.publishEvent(assetTagEvent);
    }
}
