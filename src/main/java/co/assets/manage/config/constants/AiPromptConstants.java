package co.assets.manage.config.constants;

public interface AiPromptConstants {

    String IDENTIFY_TAG_PROMPT = """
            以下の画像について、許可されたタグの中から該当するタグを選び、
            結果を JSON 形式で返してください。
            `{"标签": 置信度, …}` のようにしてください。
            """;

}
