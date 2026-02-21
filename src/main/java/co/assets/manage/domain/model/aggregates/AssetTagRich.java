package co.assets.manage.domain.model.aggregates;

import co.assets.manage.domain.model.AssetTagDO;
import co.assets.manage.enums.TagSourceEnum;

import java.sql.Timestamp;

public class AssetTagRich extends AssetTagDO {

    public static AssetTagDO ofAi(Long assetId, Long tagId, Double confidence, Timestamp createTime) {
        AssetTagDO tag = new AssetTagDO();
        tag.setAssetId(assetId);
        tag.setTagId(tagId);
        tag.setSource(TagSourceEnum.AI);
        tag.setConfidenceScore(confidence);
        tag.setCreateTime(createTime);
        tag.setDeleted(Boolean.FALSE);
        return tag;
    }

}
