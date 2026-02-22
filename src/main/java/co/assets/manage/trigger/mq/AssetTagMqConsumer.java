package co.assets.manage.trigger.mq;

import co.assets.manage.config.RocketMqConfig;
import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.service.ITagService;
import co.assets.manage.utils.JsonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RocketMQMessageListener(
        topic = RocketMqConfig.Topic.ASSET_TAG_AI,
        consumerGroup = RocketMqConfig.Group.ASSET_TAG_AI,
        consumeMode = ConsumeMode.CONCURRENTLY,
        maxReconsumeTimes = 3
)
public class AssetTagMqConsumer implements RocketMQListener<AssetTagEvent> {

    @Resource
    private ITagService tagService;

    @Override
    public void onMessage(AssetTagEvent assetTagEvent) {
        log.info("接收到创建Asset mq msg:{}", JsonUtil.toJson(assetTagEvent));
        try {

            tagService.addTag(assetTagEvent.assetId(), assetTagEvent.filePath());

        } catch (Exception e) {
            log.error("接收到创建Asset mq error:{}", e.getMessage(), e);
        }
    }
}
