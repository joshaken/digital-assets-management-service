package co.assets.manage.domain;

import co.assets.manage.domain.complex.AssetTageMultiKey;
import co.assets.manage.enums.TagSourceEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_tag", indexes = {
        @Index(name = "idx_asset_tag_id", columnList = "asset_id, tag_id")
})
@Comment("アセットとタグの関連付けテーブル")
@IdClass(AssetTageMultiKey.class)
public class AssetTagDO {

    @Id
    @Column(name = "asset_id", nullable = false)
    @Comment("アセットID")
    private Long assetId;

    @Id
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

    @Column(name = "create_time")
    @Comment("作成日時")
    private LocalDateTime createTime;
}
