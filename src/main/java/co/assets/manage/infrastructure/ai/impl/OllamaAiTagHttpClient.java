package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.dto.req.OllamaLlama3VisionRequest;
import co.assets.manage.infrastructure.ai.AiTagClient;
import co.assets.manage.utils.OkHttp3Util;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Slf4j
@ConditionalOnProperty(value = "config.ai.method", havingValue = "http")
@Service
public class OllamaAiTagHttpClient implements AiTagClient {


    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;
    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;
    private final PromptTemplate userPrompt;
    private final PromptTemplate sysPrompt;

    public OllamaAiTagHttpClient(
            @Value("classpath:prompts/asset-tag-evaluation-user.st") org.springframework.core.io.Resource userPromptResource,
            @Value("classpath:prompts/asset-tag-evaluation-sys.st") org.springframework.core.io.Resource sysPromptResource
    ) throws IOException {
        this.userPrompt = new PromptTemplate(userPromptResource.getContentAsString(StandardCharsets.UTF_8));
        this.sysPrompt = new PromptTemplate(sysPromptResource.getContentAsString(StandardCharsets.UTF_8));
    }

    @Resource
    private OkHttp3Util okHttp3Util;

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, Set<String> allowedTags) {
        String userPromptTemplate = userPrompt.getTemplate();
        // Assemble a custom prompt
        String fullPrompt = userPromptTemplate.replace("{tagList}", String.join("、", allowedTags));

        OllamaLlama3VisionRequest reqBody = OllamaLlama3VisionRequest.transToRequest(
                Base64.getEncoder().encodeToString(imageBytes)
                , model
                , fullPrompt
                , sysPrompt.getTemplate()
        );

        return okHttp3Util.getTagsByPostJson(baseUrl + "/api/chat ", reqBody);
    }

}
