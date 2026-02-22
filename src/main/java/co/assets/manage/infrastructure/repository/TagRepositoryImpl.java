package co.assets.manage.infrastructure.repository;

import co.assets.manage.domain.model.po.TagDO;
import co.assets.manage.domain.repository.ITagRepository;
import co.assets.manage.infrastructure.repository.jpa.TagJPARepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagRepositoryImpl implements ITagRepository {
    @Resource
    public TagJPARepository tagJPARepository;

    @Override
    public List<TagDO> findAllNotDeletedTag() {
        return tagJPARepository.findByDeleted(Boolean.FALSE);
    }
}
