package co.assets.manage.domain.model.po;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "tag")
@Comment("タグ情報を管理するテーブル")
public class TagDO extends BaseDO {

    @Column(nullable = false, length = 100)
    @Comment("タグ名称")
    private String name;

    @Column(name = "category", length = 100)
    @Comment("カテゴリ")
    private String category;

}
