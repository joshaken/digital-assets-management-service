package co.assets.manage.trigger.task;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.enums.AiTagStatusEnum;
import co.assets.manage.service.IAssetService;
import co.assets.manage.service.ITagService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetTagTask {

    @Resource
    private IAssetRepository iAssetRepository;
    @Resource
    private ITagService iTagService;

    //这里可以使用xxl-job类似的外部的可配置的定时任务框架，这里暂时使用spring自带的定时任务
    //    @XxlJob("assetTag")
    public void assetAddTagTask() {
        //查询前30个未打标签的asset, 并且重试次数为小于2
        List<AssetDO> failedAssetList = iAssetRepository.findAssetByStatusAndRetryCount(AiTagStatusEnum.FAILED, 2, 30);
        iTagService.retryAddTag(failedAssetList);

    }

}
