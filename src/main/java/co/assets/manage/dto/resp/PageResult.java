package co.assets.manage.dto.resp;

//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author song
 */
@Data
//@Schema(description = "分页的查询结果")
public class PageResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3669714062005657263L;

    /**
     * 当前页
     */
//    @Schema(description = "当前页", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNow;
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

    public void setCount(Integer count) {
        if (pageSize != null) {
            this.pageCount = (pageSize == 0 || count == 0) ? 1 : (int) Math.ceil((double) count / (double) pageSize);
        }
        this.count = count;
    }

    /**
     * 记录条数
     */
//    @Schema(description = "总的记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer count;
    /**
     * 记录列表
     */
//    @Schema(description = "记录列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    public static <T> PageResult<T> empty(int pageSize) {
        PageResult<T> result = new PageResult<T>();
        result.setList(Collections.emptyList());
        result.setPageCount(0);
        result.setCount(0);
        result.setPageSize(pageSize);
        result.setPageNow(DEFAULT_PAGE_NOW);
        return result;
    }

    private static final int DEFAULT_PAGE_NOW = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    public PageResult() {
        this.pageNow = DEFAULT_PAGE_NOW;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PageResult(int pageNow, int pageSize) {
        if (pageNow < DEFAULT_PAGE_NOW) {
            pageNow = DEFAULT_PAGE_NOW;
        }
        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.pageNow = pageNow;
        this.pageSize = pageSize;
    }


    public Integer getOffset() {
        return (this.getPageNow() - 1) * this.getPageSize();
    }

    public static <T> PageResult<T> page(int count, int pageSize, int pageNow, List<T> results) {
        PageResult<T> commonPageResult = new PageResult<>();
        commonPageResult.setList(results);
        commonPageResult.setCount(count);
        commonPageResult.setPageSize(pageSize);
        commonPageResult.setPageNow(pageNow);
        // 计算总页数
        int pageCount = (pageSize == 0 || count == 0) ? 1 : (int) Math.ceil((double) count / (double) pageSize);
        commonPageResult.setPageCount(pageCount);
        return commonPageResult;
    }


    public static <T, R> PageResult<R> converter(PageResult<T> result, Function<T, R> converter) {
        List<T> results = result.getList();
        List<R> data = Objects.isNull(results) ?
                Collections.emptyList() : results.stream().map(converter).collect(Collectors.toList());
        return page(result.getCount(), result.getPageSize(), result.getPageNow(), data);
    }
}
