package co.assets.manage.domain.model.po;

import co.assets.manage.domain.BaseDomain;
import co.assets.manage.enums.AiTagStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity(name = "asset")
@Table(name = "asset")
@Comment("デジタルアセット情報を管理するテーブル")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AssetDO extends BaseDomain {

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
    private AiTagStatusEnum aiTagStatus;

    @Column(name = "ai_tag_retry_count", nullable = false)
    @Comment("AIタグ付与のリトライ回数")
    private Integer aiTagRetryCount;

    @Column(name = "update_time")
    @Comment("更新日時")
    private LocalDateTime updateTime;
}
