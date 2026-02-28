package co.assets.manage.infrastructure.storage.iml;

import co.assets.manage.infrastructure.storage.IOssQueryService;
import co.assets.manage.utils.OkHttp3Util;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImageQueryClient implements IOssQueryService {

    @Resource
    public OkHttp3Util okHttp3Util;


    @Override
    public byte[] getImage(String filePath) {

        return okHttp3Util.loadImage(filePath);
    }
}
