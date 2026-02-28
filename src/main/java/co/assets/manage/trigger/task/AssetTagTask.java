package co.assets.manage.trigger.task;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.enums.AiTagStatusEnum;
import co.assets.manage.service.ITagService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public class AssetTagTask {

    @Resource
    private IAssetRepository iAssetRepository;
    @Resource
    private ITagService iTagService;
    @Value("${config.task.retry:2}")
    private Integer retry;
    @Value("${config.task.limit:20}")
    private Integer limit;


    /**
     * タグ付けに失敗したAssetデータを定期タスクで処理, 5分ごとに実行するよう設定可能
     */
    @XxlJob("assetTag")
    public void assetAddTagTask() {
        //タグ付けに失敗したAssetのうち、リトライ回数が2未満の最初の30件を取得
        List<AssetDO> failedAssetList = iAssetRepository.findAssetByStatusAndRetryCount(AiTagStatusEnum.FAILED, retry, limit);
        if (CollectionUtils.isEmpty(failedAssetList)) {
            log.info("AssetTagTask");
            return;
        }
        iTagService.retryAddTag(failedAssetList);

    }

}
