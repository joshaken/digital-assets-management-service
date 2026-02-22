package co.assets.manage.infrastructure.repository.jpa;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
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

    @Query(value = """
            SELECT a
            FROM asset a
            WHERE a.deleted = false
              AND EXISTS (
                  SELECT 1
                  FROM asset_tag at
                  JOIN tag t ON at.tagId = t.id
                  WHERE at.assetId = a.id
                    AND at.deleted = false
                    AND t.deleted = false
                    AND t.name = :tagName
              )
            """
            , countQuery = """
            SELECT COUNT(a.id)
            FROM asset a
            WHERE a.deleted = false
              AND EXISTS (
                  SELECT 1
                  FROM asset_tag at
                  JOIN tag t ON at.tagId = t.id
                  WHERE at.assetId = a.id
                    AND at.deleted = false
                    AND t.deleted = false
                    AND t.name = :tagName
              )
            """
    )
    Page<AssetDO> searchByTagName(@Param("tagName") String tagName, Pageable pageable);

    @Query(value = "select * from asset where ai_tag_status= :status and deleted=false and ai_tag_retry_count< :retryCount limit :lim"
            , nativeQuery = true)
    List<AssetDO> findAssetByStatusAndRetryCount(@Param("status") String status
            , @Param("retryCount") Integer retryCount
            , @Param("lim") Integer lim);

    @Query(value = """
            SELECT a.*
            FROM asset a
            WHERE a.deleted = false
              AND a.id > :lastPageMaxId
              AND EXISTS (
                  SELECT 1
                  FROM asset_tag at
                  WHERE at.asset_id = a.id
                    AND at.tag_id = :tagId
                    AND at.deleted = false
              )
            ORDER BY a.id
            LIMIT :pageSize
            """, nativeQuery = true)
    List<AssetDO> findByMinIdAndLimit(
            @Param("tagId") Long tagId,
            @Param("lastPageMaxId") Long lastPageMaxId,
            @Param("pageSize") Integer pageSize
    );

    @Query(value = """
            SELECT COUNT(1)
            FROM asset_tag at
            WHERE at.tag_id = :tagId
              AND at.deleted = false
              AND EXISTS (
                  SELECT 1
                  FROM asset a
                  WHERE a.id = at.asset_id
                    AND a.deleted = false
              )
            """, nativeQuery = true)
    Long countByTagId(@Param("tagId") Long tagId);
}
