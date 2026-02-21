package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.AssetDO;
import co.assets.manage.enums.AiTagStatusEnum;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAssetRepository {

    AssetDO save(AssetDO asset);

    void updateTagStatus(Long assetId, AiTagStatusEnum aiTagStatusEnum);

    Page<AssetDO> searchByTagName(String tagName, Integer pageIndex, Integer pageSize);
}
