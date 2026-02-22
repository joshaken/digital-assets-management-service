package co.assets.manage.utils;

import co.assets.manage.config.exception.ForwardServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Component
public class OkHttp3Util {

    @Resource
    private OkHttpClient okHttpClient;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");


    /**
     * 根据 URL 下载图片并转换为 byte[]
     *
     * @param imageUrl 图片链接
     * @return 图片字节数组
     * @throws IOException 下载失败时抛出异常
     */
    public byte[] downloadImage(String imageUrl) {
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

    public ResponseBody getFile(String url) {
        log.info("getFile [{}]", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body();
        } catch (Exception e) {
            log.error("okhttp getFile {}", e.getMessage(), e);
            return null;
        }
//        return okHttpClient.newCall(request).execute().body();
    }


    public String getFilePicBase64(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
//            return getRequestBody(response);
                if (response.body() != null) {
                    return Base64.getEncoder().encodeToString(response.body().bytes());
                }
                return null;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (Exception e) {
            log.error("okhttp postByJson " + e.getMessage());
            return null;
        }
    }


    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public String get(String url, Map<String, String> queries) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request
                .Builder()
                .url(sb.toString())
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            int status = response.code();
            if (status == 200) {
                assert response.body() != null;
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp put error >> ex = ", e);
        }
        return responseBody;
    }

    public String get(String url) {
        Request request = new Request
                .Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            int status = response.code();
            if (status == 200) {
                assert response.body() != null;
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp put error >> ex = ", e);
        }
        return "";
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
                return "Unexpected code " + response;
            }
        } catch (Exception e) {
            log.error("okhttp postByJson {}", e.getMessage(), e);
            return "";
        }
    }


}
