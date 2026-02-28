package co.assets.manage.infrastructure.ai;

import co.assets.manage.config.exception.BizException;
import co.assets.manage.enums.AssetProcessStepEnum;
import co.assets.manage.service.workflow.AssetProcessHandler;
import co.assets.manage.service.workflow.AssetProcessingContext;

import java.util.Map;
import java.util.Set;

public interface IAiTagService extends AssetProcessHandler {

    /**
     * AIを呼び出してタグを生成する汎用メソッド
     *
     * @param imageBytes  Image binary data
     * @param allowedTags 本システムで許可されているタグの集合
     * @return Map<tagName, confidence> タグと信頼度のMap
     */
    Map<String, Double> identifyTags(byte[] imageBytes, Set<String> allowedTags);

    /**
     * 外部AIサービスを呼び出して画像のタグを取得
     *
     * @param context 　チェーン・オブ・レスポンシビリティ のコンテキスト
     */
    @Override
    default void process(AssetProcessingContext context) {
        if (!context.getSuccess()) {
            return;
        }
        try {
            Map<String, Double> tagsConfidenceMap = identifyTags(
                    context.getImage()
                    , context.getTagIdMap().keySet());

            context.setTagsConfidenceMap(tagsConfidenceMap);

        } catch (Exception e) {
            String error = "IdentifyTags failed: " + e.getMessage();
            context.addFailReason(error);
            throw new BizException(error);
        }
    }

    @Override
    default AssetProcessStepEnum getStep() {
        return AssetProcessStepEnum.AI_TAG;
    }
}
