package co.assets.manage.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.ai.ollama.api.OllamaApi;

import java.util.List;

/*
ollama model website url
https://ollama.com/library/llama3.2-vision

cURL:
curl http://localhost:11434/api/chat -d '{
  "model": "llama3.2-vision",
  "messages": [
    {
      "role": "user",
      "content": "what is in this image?",
      "images": ["<base64-encoded image data>"]
    }
  ]
}'
 */
@NoArgsConstructor
@Getter
@Setter
public class OllamaLlama3VisionRequest {

    private String model;
    private List<Message> messages;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private List<String> images;
    }

    /**
     * HTTP方式でAIインターフェースを呼び出すための、汎用リクエスト生成メソッド
     *
     * @param base64Image 画像のBase64文字列
     * @param model       AIモデル名
     * @param userPrompt  AIのprompt
     * @return OllamaLlama3VisionRequest
     */
    public static OllamaLlama3VisionRequest transToRequest(String base64Image, String model, String userPrompt, String sysPrompt) {

        OllamaLlama3VisionRequest.Message userMessage = new OllamaLlama3VisionRequest.Message();
        userMessage.setRole(OllamaApi.Message.Role.USER.name());
        userMessage.setContent(userPrompt);
        userMessage.setImages(List.of(base64Image));

        OllamaLlama3VisionRequest.Message sysMessage = new OllamaLlama3VisionRequest.Message();
        sysMessage.setRole(OllamaApi.Message.Role.SYSTEM.name());
        sysMessage.setContent(sysPrompt);

        sysMessage.setContent(userPrompt);
        OllamaLlama3VisionRequest requestObj = new OllamaLlama3VisionRequest();
        requestObj.setModel(model);
        requestObj.setMessages(List.of(userMessage, sysMessage));

        return requestObj;
    }
}
