package co.assets.manage.domain.model.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class AssetsQueryCondition {
    private String tag;

    private Long tagId;
    //页数
    private Integer pageIndex;
    //每页大小
    private Integer pageSize;

    //上页最大ID
    private Long lastPageMaxId;

}
