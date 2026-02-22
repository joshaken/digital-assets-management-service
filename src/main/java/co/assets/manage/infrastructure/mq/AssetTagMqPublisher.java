package co.assets.manage.infrastructure.mq;

import co.assets.manage.config.RocketMqConfig;
import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.infrastructure.ai.AssetAddTagPublisher;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@ConditionalOnProperty(value = "config.tag.add", havingValue = "mq")
public class AssetTagMqPublisher implements AssetAddTagPublisher {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void sendCreateAssetEvent(AssetTagEvent assetTagEvent) {
        SendResult sendResult = null;
        try {
            sendResult = rocketMQTemplate.syncSend(
                    RocketMqConfig.Topic.ASSET_TAG_AI,
                    MessageBuilder.withPayload(assetTagEvent)
                            .build()
            );

        } catch (Exception e) {
            log.error("send asset tag mq error : {}", e.getMessage(), e);
        } finally {
            log.info("send asset tag mq input [{}], result [{}]", assetTagEvent, sendResult);
        }

    }

}
