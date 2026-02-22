package co.assets.manage.dto.resp;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * ページング検索結果のラッパークラス
 */
@Data
public class PageResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3669714062005657263L;

    /**
     * 現在ページ index
     */
    private Integer pageIndex;
    /**
     * 総ページ数
     */
    private Integer pageCount;

    /**
     * 1ページあたりの件数
     */
    private Integer pageSize;

    /**
     * データリスト
     */
    private List<T> list;
    /**
     * 総件数
     */
    private Integer count;

    public static <T> PageResult<T> page(int count, int pageSize, int pageIndex, List<T> results) {
        PageResult<T> commonPageResult = new PageResult<>();
        commonPageResult.setPageSize(pageSize);
        commonPageResult.setCount(count);
        commonPageResult.setPageIndex(pageIndex);
        commonPageResult.setList(results);
        // 総ページ数を計算
        Integer pageCount = (pageSize == 0 || count == 0) ? 1 : (int) Math.ceil((double) count / (double) pageSize);
        commonPageResult.setPageCount(pageCount);
        return commonPageResult;
    }

}
