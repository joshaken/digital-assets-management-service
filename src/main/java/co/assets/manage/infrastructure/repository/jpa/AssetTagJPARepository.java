package co.assets.manage.infrastructure.repository.jpa;

import co.assets.manage.domain.model.po.AssetTagDO;
import co.assets.manage.infrastructure.repository.batch.CustomBatchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (asset_tag) データベースとやり取りするJPAの実装
 */
@Repository
public interface AssetTagJPARepository extends JpaRepository<AssetTagDO, Long>
        , JpaSpecificationExecutor<AssetTagDO>
        , CustomBatchRepository<AssetTagDO> {

    List<AssetTagDO> findByAssetIdAndDeletedFalse(Long id);
}
