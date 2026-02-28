package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.config.exception.ForwardServiceException;
import co.assets.manage.infrastructure.ai.IAiTagService;
import co.assets.manage.utils.CustomStringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@ConditionalOnProperty(value = "config.ai.method", havingValue = "spring")
@Service
public class OllamaAiTagSpringAIClient implements IAiTagService {


    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    private final PromptTemplate userPrompt;
    private final PromptTemplate sysPrompt;
    private final BeanOutputConverter<Map<String, Double>> outputConverter;

    @Resource
    private OllamaApi ollamaApi;

    public OllamaAiTagSpringAIClient(ChatClient.Builder chatClientBuilder
            , @Value("classpath:prompts/asset-tag-evaluation-user.st") org.springframework.core.io.Resource userPromptResource
            , @Value("classpath:prompts/asset-tag-evaluation-sys.st") org.springframework.core.io.Resource sysPromptResource
    ) throws IOException {
        this.sysPrompt = new PromptTemplate(sysPromptResource.getContentAsString(StandardCharsets.UTF_8));
        this.userPrompt = new PromptTemplate(userPromptResource.getContentAsString(StandardCharsets.UTF_8));
        this.outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<Map<String, Double>>() {
        });
    }

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, Set<String> allowedTags) {
        //  タグリストの文字列を組み立てる
        String tagListStr = String.join("、", allowedTags);

        // テンプレート内の{tagList}プレースホルダーを置換する
        String renderedPrompt = userPrompt.getTemplate().replace("{tagList}", tagListStr);
        // Spring AI の API を使用する
        String outputText;
        try {

            OllamaApi.Message sysMessage = OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM)
                    .content(sysPrompt.getTemplate())
                    .build();
            OllamaApi.Message userMessage = OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                    .content(renderedPrompt)
                    .images(List.of(Base64.getEncoder().encodeToString(imageBytes)))
                    .build();

            OllamaApi.ChatRequest request = OllamaApi.ChatRequest.builder(model)
                    .messages(List.of(sysMessage, userMessage))
                    .build();
            //Ollamaを呼び出す
            outputText = ollamaApi.chat(request)
                    .message()
                    .content();

        } catch (Exception e) {
            // ここではリトライロジック、フォールバックロジック、ログを追加可能
            throw new ForwardServiceException("Ollama vision call failed " + e.getMessage());
        }
        //  空チェック（モデルがnullや空のレスポンスを返さないようにする）
        if (!StringUtils.hasLength(outputText) || outputText.trim().isEmpty()) {
            throw new ForwardServiceException("Ollama's response is empty");
        }

        log.info("OllamaAiTagSpringAIClient identifyTags output {}", outputText);
        // 解析为 Map<String, Double>
        try {
            return outputConverter.convert(outputText);
        } catch (Exception e) {
            log.error("Failed to parse tags, output as Map<String, Double> (original output: {}) [try extract json]", outputText);
            return outputConverter.convert(CustomStringUtils.extractPureJson(outputText));
        }
    }
}
