package co.assets.manage.infrastructure.ai;

import java.util.Map;
import java.util.Set;

public interface AiTagClient {

    /**
     * AIを呼び出してタグを生成する汎用メソッド
     *
     * @param imageBytes  Image binary data
     * @param allowedTags 本システムで許可されているタグの集合
     * @return Map<tagName, confidence> タグと信頼度のMap
     */
    Map<String, Double> identifyTags(byte[] imageBytes, Set<String> allowedTags);
}
