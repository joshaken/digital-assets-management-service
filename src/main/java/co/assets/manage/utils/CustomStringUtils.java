package co.assets.manage.utils;

import co.assets.manage.config.exception.BizException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.MediaType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomStringUtils {
    /**
     * 将输入字符串截取为最大长度限制
     *
     * @param input     待处理的字符串
     * @param maxLength 最大长度，例如 500
     * @return 截取后的字符串，如果 input 为 null，返回 null
     */
    public static String truncateToMaxLength(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength);
    }

    public static String truncateToFailReasonMaxLen(String input) {
        return truncateToMaxLength(input, 500);
    }


    /**
     * 根据文件路径或文件名后缀返回对应的 MediaType 值
     * 支持常见图像：jpg/jpeg, png, gif, webp 等
     * 默认返回 image/jpeg
     */
    public static String getMimeTypeFromExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return MediaType.IMAGE_JPEG_VALUE; // 默认 JPEG
        }

        String fileName = filePath.toLowerCase(Locale.ROOT);
        String extension = "";

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1);
        }

        return switch (extension) {
            case "jpg", "jpeg", "jpe" -> MediaType.IMAGE_JPEG_VALUE;     // image/jpeg
            case "png" -> MediaType.IMAGE_PNG_VALUE;      // image/png
            case "gif" -> MediaType.IMAGE_GIF_VALUE;      // image/gif
            case "webp" -> "image/webp";                    // Ollama
            case "bmp" -> "image/bmp";
            default -> MediaType.IMAGE_JPEG_VALUE;     //  JPEG
        };
    }

    /**
     * 提前字符串总的json
     *
     * @param str
     * @return
     */
    public static String extractPureJson(String str) {
        Pattern jsonPattern = Pattern.compile("\\{.*?}", Pattern.DOTALL);
        Matcher matcher = jsonPattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new BizException("未找到 JSON 输出, 原始输出：" + str);
        }
    }

}
