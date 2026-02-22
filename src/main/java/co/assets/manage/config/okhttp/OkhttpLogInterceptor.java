package co.assets.manage.config.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import java.nio.charset.StandardCharsets;

@Slf4j
public class OkhttpLogInterceptor implements Interceptor {
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    // 1MB、OOMを回避
    private static final long MAX_LOG_BODY_SIZE = 1024 * 1024;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // リクエストの基本情報を記録（bodyは読み込まない）
        boolean logRequest = log.isInfoEnabled();
        boolean logResponse = log.isInfoEnabled();

        if (logRequest) {
            logRequest(request);
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log.warn("HTTP request failed: {} {}", request.method(), request.url(), e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        if (logResponse) {
            logResponse(response, tookMs);
        }

        return response;
    }

    private void logRequest(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n---------- HTTP Request ----------\n");
        sb.append(request.method()).append(' ').append(request.url()).append('\n');

        // Headers
        Headers headers = request.headers();
        for (int i = 0, size = headers.size(); i < size; i++) {
            String name = headers.name(i);
            // 機密ヘッダー（例：Authorization）の出力を避け
            if ("Authorization".equalsIgnoreCase(name) || "Cookie".equalsIgnoreCase(name)) {
                sb.append(name).append(": *****\n");
            } else {
                sb.append(name).append(": ").append(headers.value(i)).append('\n');
            }
        }

        // Body (only if present and small enough)
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            try {
                // Only read if body is repeatable (most are, but not all)
                requestBody.writeTo(buffer);
                String body = getTruncatedString(buffer, requestBody.contentType());
                sb.append("Body:\n").append(body).append('\n');
            } catch (IOException e) {
                sb.append("Body: [Failed to read body]\n");
            }
        }

        sb.append("---------- End HTTP Request ----------");
        log.info(sb.toString());
    }

    private void logResponse(Response response, long tookMs) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n---------- HTTP Response ----------\n");
        sb.append(response.code())
                .append(' ')
                .append(response.message())
                .append(" (")
                .append(tookMs)
                .append("ms)")
                .append('\n');
        sb.append("URL: ").append(response.request().url()).append('\n');

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            BufferedSource source = responseBody.source();
            try {
                // Peek entire body (up to MAX_LOG_BODY_SIZE)
                // clone to avoid consuming original
                String body = source.peek().readUtf8(Math.min(source.getBuffer().size(), MAX_LOG_BODY_SIZE));
                ;
                sb.append("Body:\n").append(body).append('\n');
            } catch (IOException e) {
                sb.append("Body: [Failed to read response body]\n");
            }
        }

        sb.append("---------- End HTTP Response ----------");
        log.info(sb.toString());
    }

    private String getTruncatedString(Buffer buffer, MediaType contentType) throws EOFException {
        Charset charset = UTF8;
        if (contentType != null) {
            try {
                Charset parsedCharset = contentType.charset(UTF8);
                if (parsedCharset != null) {
                    charset = parsedCharset;
                }
            } catch (Exception ignored) {
                // Keep default UTF-8 — no need to reassign
            }
        }

        long byteCount = Math.min(buffer.size(), MAX_LOG_BODY_SIZE);
        String body = buffer.readString(byteCount, charset);

        if (buffer.size() > MAX_LOG_BODY_SIZE) {
            body += "\n[Body truncated. Max log size: " + (MAX_LOG_BODY_SIZE / 1024) + " KB]";
        }

        return body;
    }
}