package co.assets.manage.infrastructure.repository.batch;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CustomBatchRepository<T> {

    /**
     * 批量保存
     * @param entities
     * @return
     */
    List<T> batchSave(Collection<T> entities);

    /**
     * 批量修改
     * @param entities
     * @return
     */
    int batchUpdate(Collection<T> entities);
}
