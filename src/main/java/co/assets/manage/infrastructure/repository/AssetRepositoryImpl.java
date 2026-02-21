package co.assets.manage.infrastructure.repository;

import co.assets.manage.domain.model.AssetDO;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.enums.AiTagStatusEnum;
import co.assets.manage.infrastructure.repository.jpa.AssetJPARepository;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetRepositoryImpl implements IAssetRepository {

    @Resource
    private AssetJPARepository assetJPARepository;

    @Override
    public AssetDO save(AssetDO asset) {
        return assetJPARepository.save(asset);
    }

    @Override
    public void updateTagStatus(Long assetId, AiTagStatusEnum aiTagStatusEnum) {
        assetJPARepository.updateTagStatus(assetId, aiTagStatusEnum.name());
    }

    @Override
    public Page<AssetDO> searchByTagName(String tagName, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return assetJPARepository.searchByTagName(tagName, pageable);
    }
}
