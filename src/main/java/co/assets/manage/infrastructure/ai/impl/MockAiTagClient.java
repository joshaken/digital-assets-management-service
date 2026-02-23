package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.infrastructure.ai.AiTagClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * AIインターフェースの呼び出しをシミュレート、インターフェースロジックのテストに使用可能
 */
@Service
@ConditionalOnProperty(value = "config.ai.method", havingValue = "mock", matchIfMissing = true)
public class MockAiTagClient implements AiTagClient {
    private final Random random = new Random();

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, Set<String> allowedTags) {
        Map<String, Double> result = new HashMap<>(allowedTags.size());

        // allowedTagsからランダムにいくつか選択して、AIによるタグ付けをシミュレート
        for (String tag : allowedTags) {
            // 50% 概率选择
            if (random.nextBoolean()) {
                // 0.5~1.0
                double confidence = 0.5 + random.nextDouble() * 0.5;
                result.put(tag, confidence);
            }
        }


        return result;
    }
}
