package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.AssetTagDO;

import java.util.List;

public interface IAssetTagRepository {

    void batchCreate(List<AssetTagDO> list);

}
