package co.assets.manage.domain.model;


import co.assets.manage.domain.BaseDomain;
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
@Entity(name = "tag")
@Table(name = "tag", indexes = {
        @Index(name = "idx_parent", columnList = "parent_id")
})
@Comment("タグ情報を管理するテーブル")
public class TagDO extends BaseDomain {

    @Column(nullable = false, length = 100)
    @Comment("タグ名称")
    private String name;

    @Column(name = "parent_id")
    @Comment("親タグID（階層構造用、NULL可）")
    private Long parentId;

}
