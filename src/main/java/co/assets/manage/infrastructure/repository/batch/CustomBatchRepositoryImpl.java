package co.assets.manage.infrastructure.repository.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Spring JPAの拡張。複数のSQLを実行するのではなく、真のバッチ保存を可能にする。
 * デフォルトでは一度に50件保存。
 *
 * @param <T>
 * @param <ID>
 */
@Service
public class CustomBatchRepositoryImpl<T, ID extends Serializable> implements CustomBatchRepository<T> {

    @Value("${batchSave.size:50}")
    private Integer BATCH_SIZE;

    @PersistenceContext
    private EntityManager em;

    @Transactional(rollbackFor = Exception.class)
    public List<T> batchSave(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        Iterator<T> iterator = entities.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            em.persist(iterator.next());
            index++;
            if (index % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
        if (index % BATCH_SIZE != 0) {
            em.flush();
            em.clear();
        }
        return new ArrayList<>(entities);
    }

    @Transactional(rollbackFor = Exception.class)
    public int batchUpdate(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        Iterator<T> iterator = entities.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            em.merge(iterator.next());
            index++;
            if (index % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
        if (index % BATCH_SIZE != 0) {
            em.flush();
            em.clear();
        }
        return entities.size();
    }
}
