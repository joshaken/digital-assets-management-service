package co.assets.manage.dto.resp;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class OllamaAiResponse {
    private Map<String, Double> tagConfidence;
}
