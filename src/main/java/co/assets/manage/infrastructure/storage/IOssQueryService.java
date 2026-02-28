package co.assets.manage.infrastructure.storage;

import co.assets.manage.config.exception.BizException;
import co.assets.manage.enums.AssetProcessStepEnum;
import co.assets.manage.service.workflow.AssetProcessHandler;
import co.assets.manage.service.workflow.AssetProcessingContext;

public interface IOssQueryService extends AssetProcessHandler {

    /**
     * OkHttpを使って画像のバイナリデータを取得
     * 後で、OSSサービスにアクセスするように変更可能
     *
     * @param filePath filePath
     * @return file byte[] data
     */
    byte[] getImage(String filePath);

    /**
     * 画像情報を読み込む process
     *
     * @param context 　チェーン・オブ・レスポンシビリティ のコンテキスト
     */
    @Override
    default void process(AssetProcessingContext context) {
        try {
            byte[] image = getImage(context.getFilePath());
            context.setImage(image);

        } catch (Exception e) {
            String error = "GetImage failed: " + e.getMessage();
            context.addFailReason(error);
            throw new BizException(error);
        }
    }

    @Override
    default AssetProcessStepEnum getStep() {
        return AssetProcessStepEnum.OSS_FETCH;
    }
}
