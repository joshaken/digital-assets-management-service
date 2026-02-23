package co.assets.manage.domain.model.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

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

    public Integer getOffset() {
        int page = Optional.ofNullable(pageIndex).filter(p -> p >= 1).orElse(1);
        int size = Optional.ofNullable(pageSize).filter(s -> s >= 1).orElse(20);
        return (page - 1) * size;
    }


}
