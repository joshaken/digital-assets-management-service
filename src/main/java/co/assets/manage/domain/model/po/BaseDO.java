package co.assets.manage.domain.model.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

/**
 * データテーブルの共通カラムマッピングクラス
 */
@Getter
@Setter
@ToString
@MappedSuperclass
@NoArgsConstructor
public class BaseDO {
    /**
     * 主キー
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 作成日時
     */
    @Column(name = "create_time")
    @CreatedDate
    private Timestamp createTime;

    /**
     * 論理削除日時（削除された場合のみ設定）
     */
    @Column(name = "delete_time")
    private Timestamp deleteTime;

    /**
     * 論理削除フラグ（0：有効、1：削除済み）
     * mysql ：true: 1 false:0
     */
    private Boolean deleted;
}