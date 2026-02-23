package co.assets.manage.service;

import co.assets.manage.domain.model.po.AssetDO;

import java.util.List;

public interface ITagService {
    /**
     * Assetにタグを追加
     *
     * @param assetId  assetId
     * @param filePath asset's filePath
     */
    void addTag(Long assetId, String filePath);

    /**
     * Assetへのタグ追加を再試行
     *
     * @param assetList assetList
     */
    void retryAddTag(List<AssetDO> assetList);
}
