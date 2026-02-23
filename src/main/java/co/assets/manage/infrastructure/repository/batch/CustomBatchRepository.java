package co.assets.manage.infrastructure.repository.batch;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CustomBatchRepository<T> {

    /**
     * 一括保存
     */
    List<T> batchSave(Collection<T> entities);

    /**
     * 一括更新
     */
    int batchUpdate(Collection<T> entities);
}
