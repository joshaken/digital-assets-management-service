package co.assets.manage.service;

import co.assets.manage.domain.model.po.AssetDO;

import java.util.List;

public interface ITagService {
    void addTag(Long assetId, String filePath);

    void retryAddTag(List<AssetDO> assetDOList);
}
