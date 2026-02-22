package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.infrastructure.ai.AiTagClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
@ConditionalOnProperty(value = "config.ai", havingValue = "mock", matchIfMissing = true)
public class MockAiTagClient implements AiTagClient {
    private final Random random = new Random();

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, String mimeType, Set<String> allowedTags) {
        Map<String, Double> result = new HashMap<>(allowedTags.size());

        // 随机从 allowedTags 里选择若干个，模拟 AI 打标签
        for (String tag : allowedTags) {
            if (random.nextBoolean()) { // 50% 概率选择
                double confidence = 0.5 + random.nextDouble() * 0.5; // 0.5~1.0
                result.put(tag, confidence);
            }
        }


        return result;
    }
}
