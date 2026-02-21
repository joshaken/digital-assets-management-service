package co.assets.manage.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "tag", indexes = {
        @Index(name = "idx_parent", columnList = "parent_id")
})
@Comment("タグ情報を管理するテーブル")
public class TagDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("タグID（主キー）")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("タグ名称")
    private String name;

    @Column(name = "parent_id")
    @Comment("親タグID（階層構造用、NULL可）")
    private Long parent;

    @Column(name = "create_time")
    @Comment("作成日時")
    private LocalDateTime createTime;
}
