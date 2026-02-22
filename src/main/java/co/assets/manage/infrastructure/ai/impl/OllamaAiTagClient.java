package co.assets.manage.infrastructure.ai.impl;

import co.assets.manage.infrastructure.ai.AiPromptTemplate;
import co.assets.manage.infrastructure.ai.AiTagClient;
import co.assets.manage.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ConditionalOnProperty(value = "config.ai", havingValue = "ollama")
@Service
public class OllamaAiTagClient implements AiTagClient {

    private static final String BASE_URL = "http://localhost:11434";
    private static final String MODEL = "llama2‑vision"; // 已 pull 的模型名

    @Resource
    private OkHttpClient httpClient = new OkHttpClient();

    @Override
    public Map<String, Double> identifyTags(byte[] imageBytes, Set<String> allowedTags) {
        String fullPrompt = AiPromptTemplate.buildTaggingPrompt(allowedTags);

        // 构造 multipart 请求
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("model", MODEL)
                .addFormDataPart("prompt", fullPrompt)
                // 图像字段
                .addFormDataPart("image", "image.png",
                        RequestBody.create(imageBytes, MediaType.parse("image/png")))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/predict")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Ollama error: " + response.code() + " " + response.message());
            }

            String responseText = Objects.requireNonNull(response.body()).string();
            return (parseTagsFromOutput(responseText, allowedTags));

        } catch (IOException ex) {
            throw new RuntimeException("Ollama request failed", ex);
        }
    }

    private Map<String, Double> parseTagsFromOutput(String output, Set<String> allowedTags) {
        try {
            //  response JSON transfer to Map<String, Double>
            return JsonUtil.toObject(output, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse AI output: {}", output, e);
            return Collections.emptyMap();
        }
    }
}
