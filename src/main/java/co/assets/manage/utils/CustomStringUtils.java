package co.assets.manage.utils;

import co.assets.manage.config.exception.BizException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.MediaType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomStringUtils {
    /**
     * 入力文字列を最大長に制限して切り取る
     *
     * @param input     処理対象の文字列
     * @param maxLength 最大長
     * @return 切り取った文字列。inputがnullの場合はnullを返す
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
     * ファイルパスやファイル名の拡張子に基づいて対応するMediaTypeを返す
     * 一般的な画像形式に対応：jpg/jpeg, png, gif, webpなど
     * デフォルトはimage/jpegを返す
     */
    public static String getMimeTypeFromExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return MediaType.IMAGE_JPEG_VALUE;
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
            case "webp" -> "image/webp";
            case "bmp" -> "image/bmp";
            default -> MediaType.IMAGE_JPEG_VALUE;     //  JPEG
        };
    }

    /**
     * 文字列から最初の完全なJSONを抽出
     *
     * @param str string
     * @return json str
     */
    public static String extractPureJson(String str) {
        Pattern jsonPattern = Pattern.compile("\\{.*?}", Pattern.DOTALL);
        Matcher matcher = jsonPattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new BizException("JSON not found, original output:" + str);
        }
    }

}
