package co.assets.manage.infrastructure.repository.jpa;

import co.assets.manage.domain.model.po.TagDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagJPARepository extends JpaRepository<TagDO, Long>, JpaSpecificationExecutor<TagDO> {

    @Query(value = "select t from tag t where t.deleted= :deleted")
    List<TagDO> findByDeleted(@Param("deleted") Boolean deleted);

    @Query(value = "select id from tag where name= :name and deleted= false ")
    Long findIdByName(@Param("name") String name);
}
