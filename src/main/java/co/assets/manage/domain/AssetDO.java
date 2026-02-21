package co.assets.manage.domain;

import co.assets.manage.enums.AiTagStatusEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset", indexes = {
        @Index(name = "idx_enterprise", columnList = "enterprise_id"),
        @Index(name = "idx_create_time", columnList = "create_time"),
        @Index(name = "idx_tag_retry", columnList = "ai_tag_status, ai_tag_retry_count")
})
@Comment("デジタルアセット情報を管理するテーブル")
public class AssetDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("アセットID（主キー）")
    private Long id;

    @Column(name = "enterprise_id")
    @Comment("所属企業ID（マルチテナント対応）")
    private Long enterpriseId;

    @Column(nullable = false, length = 255)
    @Comment("アセットタイトル")
    private String title;

    @Column(name = "file_path", nullable = false, length = 500)
    @Comment("ファイル保存パス")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_tag_status", nullable = false, length = 20)
    @Comment("AIタグ付与状態（PENDING / PROCESSING / COMPLETED / FAILED）")
    private AiTagStatusEnum aiTagStatus = AiTagStatusEnum.PENDING;

    @Column(name = "ai_tag_retry_count", nullable = false)
    @Comment("AIタグ付与のリトライ回数")
    private Integer aiTagRetryCount = 0;

    @Column(name = "create_time", updatable = false)
    @Comment("作成日時")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Comment("更新日時")
    private LocalDateTime updateTime;
}
