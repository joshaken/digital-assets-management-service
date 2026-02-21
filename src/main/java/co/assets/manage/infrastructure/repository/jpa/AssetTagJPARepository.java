package co.assets.manage.infrastructure.repository.jpa;

import co.assets.manage.domain.model.AssetTagDO;
import co.assets.manage.infrastructure.repository.batch.CustomBatchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetTagJPARepository extends JpaRepository<AssetTagDO, Long>
        , JpaSpecificationExecutor<AssetTagDO>
        , CustomBatchRepository<AssetTagDO> {
}
