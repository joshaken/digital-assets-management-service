package co.assets.manage.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QueryAssetRequest(

        /*
         * ここでは、前端から直接タグのIDを渡すことができ、後続の結合クエリで1つのテーブルを減らせる
         */
        @NotBlank(message = "タグは必須項目です")
        @Size(min = 1, max = 100, message = "タグは100文字以内で入力してください")
        String tag,

        //ページ番号
        Integer pageIndex,
        //1ページあたりの件数
        Integer pageSize,
        //前ページの最大ID
        Long lastPageMaxId
) {

    public QueryAssetRequest {
        // pageIndex デフォルト値を設定
        if (pageIndex == null || pageIndex < 0) {
            pageIndex = 1;
        }
        // pageSize デフォルト値を設定
        if (pageSize == null || pageSize > 20) {
            pageSize = 20;
        }
    }
}
