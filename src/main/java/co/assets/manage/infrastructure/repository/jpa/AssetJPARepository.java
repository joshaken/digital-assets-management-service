package co.assets.manage.infrastructure.repository.jpa;

import co.assets.manage.domain.model.po.AssetDO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetJPARepository extends JpaRepository<AssetDO, Long>, JpaSpecificationExecutor<AssetDO> {

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "update asset set aiTagStatus= :status,updateTime=now() where id= :assetId")
    int updateTagStatus(@Param("assetId") Long assetId, @Param("status") String status);

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "update asset set aiTagStatus= :status,updateTime=now(),aiTagFailReason= :failReason where id= :assetId")
    int updateTagStatusAndFailReason(@Param("assetId") Long assetId, @Param("status") String status, @Param("failReason") String aiTagFailReason);

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "update asset set aiTagRetryCount= aiTagRetryCount+1,updateTime=now() where id= :assetId")
    void updateTagStatusAndCount(@Param("assetId") Long assetId);

    @Query(value = "SELECT a FROM asset_tag at " +
            "INNER JOIN tag t on at.tagId=t.id " +
            "INNER JOIN asset a on at.assetId=a.id " +
            "WHERE t.name = :tagName " +
            "AND t.deleted = false " +
            "AND at.deleted = false " +
            "AND a.deleted = false",
            countQuery = "SELECT COUNT(a.id) FROM asset_tag at " +
                    "INNER JOIN tag t on at.tagId=t.id " +
                    "INNER JOIN asset a on at.assetId=a.id " +
                    "WHERE t.name = :tagName " +
                    "AND t.deleted = false " +
                    "AND at.deleted = false " +
                    "AND a.deleted = false"
    )
    Page<AssetDO> searchByTagName(@Param("tagName") String tagName, Pageable pageable);

    @Query(value = "select * from asset where ai_tag_status= :status and ai_tag_retry_count< :retryCount limit :lim"
            , nativeQuery = true)
    List<AssetDO> findAssetByStatusAndRetryCount(@Param("status") String status, @Param("retryCount") Integer retryCount, @Param("lim") Integer lim);



}
