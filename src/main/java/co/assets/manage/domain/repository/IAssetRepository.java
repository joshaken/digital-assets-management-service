package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.enums.AiTagStatusEnum;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAssetRepository {

    AssetDO save(AssetDO asset);

    void updateTagStatus(Long assetId, AiTagStatusEnum aiTagStatusEnum, String aiTagFailReason);

    Page<AssetDO> pageQueryByTagName(String tagName, Integer pageIndex, Integer pageSize);

    List<AssetDO> findAssetByStatusAndRetryCount(AiTagStatusEnum aiTagStatus, Integer retryCount, Integer limit);

    void updateRetryCount(Long assetId);
}
