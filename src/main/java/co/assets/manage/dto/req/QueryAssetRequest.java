package co.assets.manage.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QueryAssetRequest(

        /*
         * 这里其实可以从前端直接传递tag的Id，方便后续联表查询时少查询一张表
         */
        @NotBlank(message = "タグは必須項目です")
        @Size(min = 1, max = 100, message = "タグは100文字以内で入力してください")
        String tag,

        //页数
        Integer pageIndex,
        //每页大小
        Integer pageSize,

        //上页最大ID
        Integer lastPageMaxId
) {

    public QueryAssetRequest {
        // 如果 page 为 null，赋默认值
        if (pageIndex == null || pageIndex < 0) {
            pageIndex = 1;
        }
        // 如果 size 为 null，赋默认值
        if (pageSize == null || pageSize > 20) {
            pageSize = 20;
        }
    }
}
