package co.assets.manage.domain.repository;

import co.assets.manage.domain.model.po.TagDO;

import java.util.List;

public interface ITagRepository {
    /**
     * Fetch all tags that are not deleted
     */
    List<TagDO> findAllNotDeletedTag();

    Long findTagIdByName(String tag);
}
