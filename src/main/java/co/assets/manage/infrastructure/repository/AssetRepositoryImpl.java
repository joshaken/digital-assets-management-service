package co.assets.manage.infrastructure.repository;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
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
    public void updateTagStatus(Long assetId, AiTagStatusEnum aiTagStatusEnum, String aiTagFailReason) {
        if (AiTagStatusEnum.SUCCESS.equals(aiTagStatusEnum)) {
            assetJPARepository.updateTagStatus(assetId, aiTagStatusEnum.name());
        } else {
            assetJPARepository.updateTagStatusAndFailReason(assetId, aiTagStatusEnum.name(), aiTagFailReason);
        }
    }

    @Override
    public void updateRetryCount(Long assetId) {
        assetJPARepository.updateTagStatusAndCount(assetId);
    }

    @Override
    public List<AssetDO> findAssetByMinId(AssetsQueryCondition queryCondition) {
        return assetJPARepository.findByMinIdAndLimit(queryCondition.getTagId(), queryCondition.getLastPageMaxId(), queryCondition.getPageSize());
    }

    @Override
    public Long countByTagId(Long tagId) {
        return assetJPARepository.countByTagId(tagId);
    }

    @Override
    public Page<AssetDO> pageQueryByTagName(String tagName, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return assetJPARepository.searchByTagName(tagName, pageable);
    }

    @Override
    public List<AssetDO> findAssetByStatusAndRetryCount(AiTagStatusEnum aiTagStatus, Integer retryCount, Integer limit) {
        return assetJPARepository.findAssetByStatusAndRetryCount(aiTagStatus.name(), retryCount, limit);
    }


}
