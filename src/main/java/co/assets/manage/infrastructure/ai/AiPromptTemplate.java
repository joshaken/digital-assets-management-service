package co.assets.manage.infrastructure.ai;

import co.assets.manage.config.constants.AiPromptConstants;

import java.util.List;
import java.util.Set;

public class AiPromptTemplate {

    public static String buildTaggingPrompt(Set<String> allowedTags) {
        String tagsStr = String.join(", ", allowedTags);

        String defaultInstruction = AiPromptConstants.IDENTIFY_TAG_PROMPT;

        return "許可タグ: " + tagsStr + "\n"
                + defaultInstruction;
    }
}
