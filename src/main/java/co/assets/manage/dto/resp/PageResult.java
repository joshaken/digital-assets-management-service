package co.assets.manage.dto.resp;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
//@Schema(description = "分页的查询结果")
public class PageResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3669714062005657263L;

    /**
     * 当前页
     */
//    @Schema(description = "当前页", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageIndex;
    /**
     * 分页记录数量
     */
//    @Schema(description = "分页记录数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageCount;

    /**
     * 每页的数据条数
     */
//    @Schema(description = "每页的数据条数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    /**
     * 记录列表
     */
    private List<T> list;


    public static <T> PageResult<T> page(int count, int pageSize, int pageIndex, List<T> results) {
        PageResult<T> commonPageResult = new PageResult<>();
        commonPageResult.setPageSize(pageSize);
        commonPageResult.setPageIndex(pageIndex);
        commonPageResult.setList(results);
        // 计算总页数
        int pageCount = (pageSize == 0 || count == 0) ? 1 : (int) Math.ceil((double) count / (double) pageSize);
        commonPageResult.setPageCount(pageCount);
        return commonPageResult;
    }

}
