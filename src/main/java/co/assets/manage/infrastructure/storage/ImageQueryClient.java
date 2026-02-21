package co.assets.manage.infrastructure.storage;

import co.assets.manage.utils.OkHttp3Util;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class ImageQueryClient {

    @Resource
    public OkHttp3Util okHttp3Util;

    public byte[] getImage(String filePath) {
       return okHttp3Util.downloadImage(filePath);
    }
}
