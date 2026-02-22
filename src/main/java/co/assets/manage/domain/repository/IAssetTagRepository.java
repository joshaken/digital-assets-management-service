package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.po.AssetTagDO;

import java.util.List;

public interface IAssetTagRepository {

    void batchCreate(List<AssetTagDO> list);

}
