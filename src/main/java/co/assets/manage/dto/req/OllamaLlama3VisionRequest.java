package co.assets.manage.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
     * @param base64Image 画像のBase64文字列
     * @param model AIモデル名
     * @param fullPrompt AIのprompt
     * @return OllamaLlama3VisionRequest
     */
    public static OllamaLlama3VisionRequest transToRequest(String base64Image, String model, String fullPrompt) {

        OllamaLlama3VisionRequest.Message message = new OllamaLlama3VisionRequest.Message();
        message.setRole("user");
        message.setContent(fullPrompt);
        message.setImages(List.of(base64Image));

        OllamaLlama3VisionRequest requestObj = new OllamaLlama3VisionRequest();
        requestObj.setModel(model);
        requestObj.setMessages(List.of(message));

        return requestObj;
    }
}
