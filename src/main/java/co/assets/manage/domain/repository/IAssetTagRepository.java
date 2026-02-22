package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.po.AssetTagDO;

import java.util.List;

public interface IAssetTagRepository {

    /**
     * アセットタグの関係データをbatch保存
     */
    void batchCreate(List<AssetTagDO> list);

}
