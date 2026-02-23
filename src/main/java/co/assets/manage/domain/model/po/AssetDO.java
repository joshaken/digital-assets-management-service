package co.assets.manage.domain.model.po;

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
public class AssetDO extends BaseDO {

    @Column(nullable = false)
    @Comment("アセットタイトル")
    private String title;

    @Column(name = "file_path", nullable = false, length = 500)
    @Comment("ファイル保存パス")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_tag_status", nullable = false, length = 20)
    @Comment("AIタグ付与状態（PENDING / COMPLETED / FAILED）")
    private AiTagStatusEnum aiTagStatus;

    @Column(name = "ai_tag_retry_count", nullable = false)
    @Comment("AIタグ付与のリトライ回数")
    private Integer aiTagRetryCount;

    @Column(name = "ai_tag_fail_reason", length = 500)
    @Comment("AIタグ付けが失敗する理由")
    private String aiTagFailReason;

    @Column(name = "update_time")
    @Comment("更新日時")
    private LocalDateTime updateTime;
}
