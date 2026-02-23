package co.assets.manage.utils;

import co.assets.manage.config.exception.ForwardServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class OkHttp3Util {

    @Resource
    private OkHttpClient okHttpClient;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");


    /**
     * URLから画像をダウンロードしてbyte[]に変換
     *
     * @param imageUrl 画像のURL
     * @return 画像 の　byte[]
     */
    public byte[] loadImage(String imageUrl) {
        Request request = new Request.Builder()
                .url(imageUrl)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ForwardServiceException("Failed to download image: " + response);
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new ForwardServiceException("Response body is null");
            }

            return body.bytes();
        } catch (Exception e) {
            throw new ForwardServiceException(e.getMessage());
        }
    }


    /**
     * @param url 请求路径
     * @param obj 请求对象
     * @return 相应字符串， 需自行序列化
     */
    public String postByJson(String url, Object obj) {
        RequestBody body = RequestBody.create(JsonUtil.toJson(obj), JSON_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return "";
            } else {
                throw new ForwardServiceException("okhttp3 postByJson error" + response);
            }
        } catch (Exception e) {
            log.error("okhttp postByJson {}", e.getMessage(), e);
            throw new ForwardServiceException("okhttp3 postByJson error" + e.getMessage());
        }
    }

    /**
     * OKHTTPリクエストを通じてAIのレスポンスを取得
     *
     * @param url     target AI URL
     * @param reqBody request body
     * @return AIのタグ応答
     */
    public Map<String, Double> getTagsByPostJson(String url, Object reqBody) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(
                        JsonUtil.toJson(reqBody),
                        JSON_TYPE
                ))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Ollama error: " + response.code() + ", body: " + response.body());
            }

            if (response.body() == null) {
                throw new RuntimeException("Empty response body from Ollama");
            }

            // すべてのresponseの断片を結合
            StringBuilder fullResponse = new StringBuilder();
            BufferedReader reader = new BufferedReader(response.body().charStream());
            String line;

            while ((line = reader.readLine()) != null) {
                // 空行をスキップ
                if (line.trim().isEmpty()) continue;

                try {
                    // 現在のNDJSON行を解析
                    JsonNode chunk = JsonUtil.readTree(line);

                    if (chunk.has("message")) {
                        JsonNode message = chunk.get("message");
                        if (message.has("content")) {
                            String content = message.get("content").asText();
                            if (StringUtils.hasLength(content)) {
                                fullResponse.append(content);
                            }
                        }
                    }
                    if (chunk.path("done").asBoolean(false)) break;

                } catch (Exception e) {
                    // 解析できない行（ログやエラー情報など）を無視
                    continue;
                }
            }

            String completeJsonStr = fullResponse.toString().trim();
            if (!StringUtils.hasLength(completeJsonStr)) {
                throw new RuntimeException("No valid response content received from Ollama");
            }

            // 最終的に解析された完全なJSON文字列をMap<String, Double>に変換
            try {
                return JsonUtil.toObj(completeJsonStr, new TypeReference<Map<String, Double>>() {
                });
            } catch (Exception e) {
                log.warn("Failed to parse original tag output into Map<String, Double>. Original output: {} try extract json ", completeJsonStr);
                return JsonUtil.toObj(CustomStringUtils.extractPureJson(completeJsonStr), new TypeReference<Map<String, Double>>() {
                });
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to call Ollama API", e);
        }
    }


}
