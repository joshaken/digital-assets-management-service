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
    private Integer pageIndex;
    /**
     * 页数量
     */
    private Integer pageCount;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 数据列表
     */
    private List<T> list;
    /**
     * 总条数
     */
    private Integer count;


    /**
     * 记录条数
     */
    public static <T> PageResult<T> page(int count, int pageSize, int pageIndex, List<T> results) {
        PageResult<T> commonPageResult = new PageResult<>();
        commonPageResult.setPageSize(pageSize);
        commonPageResult.setCount(count);
        commonPageResult.setPageIndex(pageIndex);
        commonPageResult.setList(results);
        // 计算总页数
        Integer pageCount = (pageSize == 0 || count == 0) ? 1 : (int) Math.ceil((double) count / (double) pageSize);
        commonPageResult.setPageCount(pageCount);
        return commonPageResult;
    }

}
