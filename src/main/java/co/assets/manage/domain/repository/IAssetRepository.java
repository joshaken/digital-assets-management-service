package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
import co.assets.manage.enums.AiTagStatusEnum;

import java.util.List;

public interface IAssetRepository {

    AssetDO save(AssetDO asset);

    /**
     * アセットの状態と失敗理由を更新
     *
     * @param assetId assetId
     */
    void updateTagStatus(Long assetId, AiTagStatusEnum aiTagStatusEnum, String aiTagFailReason);

    List<AssetDO> findAssetByTagId(AssetsQueryCondition queryCondition);

    List<AssetDO> findAssetByStatusAndRetryCount(AiTagStatusEnum aiTagStatus, Integer retryCount, Integer limit);

    void updateRetryCount(Long assetId);

    /**
     * Fetch next page data based on the max ID of the previous page
     * @param queryCondition ページングリクエスト
     * @return 現在ページのデータ
     */
    List<AssetDO> findAssetByMinId(AssetsQueryCondition queryCondition);

    /**
     * ページング検索で、現在の検索条件に対する総件数のみを取得
     * @param tagId tagId
     * @return 総件数
     */
    Long countByTagId(Long tagId);
}
