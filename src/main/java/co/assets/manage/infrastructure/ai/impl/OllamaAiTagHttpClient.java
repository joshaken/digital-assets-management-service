package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.dto.req.OllamaLlama3VisionRequest;
import co.assets.manage.infrastructure.ai.AiPromptTemplate;
import co.assets.manage.infrastructure.ai.AiTagClient;
import co.assets.manage.utils.OkHttp3Util;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Slf4j
@ConditionalOnProperty(value = "config.ai", havingValue = "ollama")
@Service
public class OllamaAiTagHttpClient implements AiTagClient {

    private static final String BASE_URL = "http://localhost:11434";
    private static final String MODEL = "llama3.2-vision";
    @Resource
    private OkHttp3Util okHttp3Util;

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, String mimeType, Set<String> allowedTags) {
        String fullPrompt = AiPromptTemplate.buildTaggingPrompt(allowedTags);

        OllamaLlama3VisionRequest reqBody = OllamaLlama3VisionRequest.transToRequest(
                Base64.getEncoder().encodeToString(imageBytes)
                , MODEL
                , fullPrompt
        );

        return okHttp3Util.getTagsByPostJson(BASE_URL + "/api/chat ", reqBody);
    }

}
