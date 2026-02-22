package co.assets.manage.domain.model.aggregates;

import co.assets.manage.domain.model.po.AssetTagDO;
import co.assets.manage.enums.TagSourceEnum;

import java.sql.Timestamp;

public class AssetTagRich {

    /**
     * assetIdとtagIdからAssetTagデータを生成
     *
     * @param confidence AIの応答の信頼度
     * @param createTime 作成日時を渡すことで、新しい日時クラスを作成せずに済む
     * @return AssetTagDO
     */
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
