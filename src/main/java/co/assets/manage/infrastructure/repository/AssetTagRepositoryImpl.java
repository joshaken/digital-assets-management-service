package co.assets.manage.infrastructure.repository;

import co.assets.manage.domain.model.po.AssetTagDO;
import co.assets.manage.domain.repository.IAssetTagRepository;
import co.assets.manage.infrastructure.repository.jpa.AssetTagJPARepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetTagRepositoryImpl implements IAssetTagRepository {

    @Resource
    private AssetTagJPARepository assetTagJPARepository;

    @Override
    public void batchCreate(List<AssetTagDO> list) {
        assetTagJPARepository.batchSave(list);
    }
}
