package co.assets.manage.domain.model.po;

import co.assets.manage.enums.TagSourceEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "asset_tag")
@Table(name = "asset_tag")
@Comment("アセットとタグの関連付けテーブル")
public class AssetTagDO extends BaseDO {

    @Column(name = "asset_id", nullable = false)
    @Comment("アセットID")
    private Long assetId;

    @Column(name = "tag_id", nullable = false)
    @Comment("タグID")
    private Long tagId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("タグ付与元（USER / AI）")
    private TagSourceEnum source;

    @Column(name = "confidence_score")
    @Comment("AIタグの信頼度（0.0～1.0）")
    private Double confidenceScore;

}
