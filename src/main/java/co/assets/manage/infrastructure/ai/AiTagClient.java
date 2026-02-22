package co.assets.manage.infrastructure.ai;

import java.util.Map;
import java.util.Set;

public interface AiTagClient {

    /**
     * 调用 AI 生成标签
     *
     * @param imageBytes  图片二进制
     * @param mimeType
     * @param allowedTags 允许的标签集合
     * @return Map<tagName, confidence> 标签及置信度
     */
    Map<String, Double> identifyTags(byte[] imageBytes, String mimeType, Set<String> allowedTags);
}
