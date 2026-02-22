package co.assets.manage.service;

import co.assets.manage.domain.model.po.AssetDO;
import org.springframework.data.domain.Page;

public interface IAssetService {
    /**
     * 保存asset
     * @param createAssetRequest
     */
    void create(AssetDO createAssetRequest);

    /**
     * アセット 検索
     * @param tag
     * @param page
     * @param size
     * @return
     */
    Page<AssetDO> searchByTagName(String tag, Integer page, Integer size);
}
