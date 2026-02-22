package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.config.exception.ForwardServiceException;
import co.assets.manage.infrastructure.ai.AiTagClient;
import co.assets.manage.utils.CustomStringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaModel;
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
@ConditionalOnProperty(value = "config.ai", havingValue = "spring")
@Service
public class OllamaAiTagSpringAIClient implements AiTagClient {

    private static final String MODEL = "llama3.2-vision";
    private final PromptTemplate userPromptResource;
    private final PromptTemplate sysPromptResource;
    private final BeanOutputConverter<Map<String, Double>> outputConverter;

    @Resource
    private OllamaApi ollamaApi;

    public OllamaAiTagSpringAIClient(ChatClient.Builder chatClientBuilder
            , @Value("classpath:prompts/asset-tag-evaluation-user.st") org.springframework.core.io.Resource userPromptResource
            , @Value("classpath:prompts/asset-tag-evaluation-sys.st") org.springframework.core.io.Resource sysPromptResource
    ) throws IOException {
        this.sysPromptResource = new PromptTemplate(sysPromptResource.getContentAsString(StandardCharsets.UTF_8));
        this.userPromptResource = new PromptTemplate(userPromptResource.getContentAsString(StandardCharsets.UTF_8));
        this.outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<Map<String, Double>>() {
        });
    }

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, String mimeType, Set<String> allowedTags) {
        //  准备标签列表字符串
        String tagListStr = String.join("、", allowedTags);

        // 2. 使用 PromptTemplate 渲染（推荐方式，避免手动 replace）
        // 模板中有 {tagList} 占位符
        String renderedPrompt = userPromptResource.getTemplate().replace("{tagList}", tagListStr);
        // 使用 ChatClient fluent API
        String outputText;
        try {

            OllamaApi.Message sysMessage = OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM)
                    .content(sysPromptResource.getTemplate())
                    .build();
            OllamaApi.Message userMessage = OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                    .content(renderedPrompt)
                    .images(List.of(Base64.getEncoder().encodeToString(imageBytes)))
                    .build();

            OllamaApi.ChatRequest request = OllamaApi.ChatRequest.builder(MODEL)
                    .messages(List.of(sysMessage, userMessage))
                    .build();

            outputText = ollamaApi.chat(request)
                    .message()
                    .content();

        } catch (Exception e) {
            // 这里可以加上重试逻辑、降级逻辑、日志
            throw new ForwardServiceException("Ollama vision 调用失败 " + e.getMessage());
        }
        //  空检查（防止模型返回 null 或空响应）
        if (!StringUtils.hasLength(outputText) || outputText.trim().isEmpty()) {
            throw new ForwardServiceException("Ollama 返回空响应");
        }

        log.info("output {}", outputText);
        // 解析为 Map<String, Double>
        try {
            return outputConverter.convert(outputText);
        } catch (Exception e) {
            log.error("无法解析标签输出为 Map<String, Double> 原始输出：" + outputText);
            return outputConverter.convert(CustomStringUtils.extractPureJson(outputText));
        }
    }
}
