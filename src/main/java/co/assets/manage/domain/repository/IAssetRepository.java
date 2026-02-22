package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
import co.assets.manage.enums.AiTagStatusEnum;

import java.util.List;

public interface IAssetRepository {

    AssetDO save(AssetDO asset);

    void updateTagStatus(Long assetId, AiTagStatusEnum aiTagStatusEnum, String aiTagFailReason);

    List<AssetDO> findAssetByTagId(AssetsQueryCondition queryCondition);

    List<AssetDO> findAssetByStatusAndRetryCount(AiTagStatusEnum aiTagStatus, Integer retryCount, Integer limit);

    void updateRetryCount(Long assetId);

    List<AssetDO> findAssetByMinId(AssetsQueryCondition queryCondition);

    Long countByTagId(Long tagId);
}
