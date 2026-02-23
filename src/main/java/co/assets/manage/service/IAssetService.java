package co.assets.manage.service;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
import org.springframework.data.domain.Page;

public interface IAssetService {
    /**
     * Assetを保存
     *
     * @param createAssetRequest request
     */
    void create(AssetDO createAssetRequest);

    /**
     * アセット 検索
     * lastPageMaxIdを使って特殊なページングクエリの必要性を自動判断
     *
     * @param queryCondition request
     * @return paged asset
     */
    Page<AssetDO> pageQueryByTagName(AssetsQueryCondition queryCondition);
}
