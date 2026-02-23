package co.assets.manage.infrastructure.storage;

import co.assets.manage.utils.OkHttp3Util;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class ImageQueryClient {

    @Resource
    public OkHttp3Util okHttp3Util;

    /**
     * OkHttpを使って画像のバイナリデータを取得
     * 後で、OSSサービスにアクセスするように変更可能
     *
     * @param filePath filePath
     * @return file byte[] data
     */
    public byte[] getImage(String filePath) {

        return okHttp3Util.loadImage(filePath);
    }
}
