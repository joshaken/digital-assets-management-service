package co.assets.manage.trigger.event;

import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.service.ITagService;
import co.assets.manage.utils.JsonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetTagEventConsumer {

    @Resource
    private ITagService tagService;

    /**
     * 非同期で追加されたAssetのメッセージを消費し、Assetにタグを付与
     */
    @EventListener(value = AssetTagEvent.class)
    @Async(AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public void createAsset(AssetTagEvent event) {
        log.info("AssetTagEventConsumer received an AssetTagEvent:{}", JsonUtil.toJson(event));

        tagService.addTag(event.assetId(), event.filePath());
    }
}
