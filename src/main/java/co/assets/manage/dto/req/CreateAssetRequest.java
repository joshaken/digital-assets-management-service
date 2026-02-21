package co.assets.manage.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAssetRequest(
        @NotBlank(message = "タイトルは必須項目です")
        @Size(min = 1, max = 255, message = "タイトルは255文字以内で入力してください")
        String title,

        /*
         * オブジェクトストレージに保存されたファイルの識別子を表す。
         * 将来的には保存形式（バケット名やパス形式等）に基づく
         * 詳細なフォーマット検証を実装予定である。
         * 現時点では文字数のみを検証する。
         */
        @NotBlank(message = "ファイルパスは必須項目です")
        @Size(min = 1, max = 500, message = "ファイルパスは500文字以内で入力してください")
        String filePath,

        Long enterpriseId
) {
}
