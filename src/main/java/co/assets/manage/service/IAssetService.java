package co.assets.manage.service;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
import org.springframework.data.domain.Page;

public interface IAssetService {
    /**
     * 保存asset
     *
     * @param createAssetRequest
     */
    void create(AssetDO createAssetRequest);

    /**
     * アセット 検索
     *
     * @param queryCondition
     * @return
     */
    Page<AssetDO> pageQueryByTagName(AssetsQueryCondition queryCondition);
}
