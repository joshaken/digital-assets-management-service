package co.assets.manage.service.impl;

import co.assets.manage.domain.model.AssetDO;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.infrastructure.event.EventPublisher;
import co.assets.manage.service.IAssetService;
import co.assets.manage.utils.converter.AssetConverter;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class AssetServiceImpl implements IAssetService {
    @Resource
    private IAssetRepository iAssetRepository;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void create(AssetDO createAssetRequest) {
        //保存数据库
        AssetDO newAssetDO = iAssetRepository.save(createAssetRequest);
        //异步调用第三方AI，这里使用一个通用接口，后续可以改成发送MQ消息，在消费方再进行外部AI接口调用
        eventPublisher.sendCreateAssetEvent(AssetConverter.INSTANCE.transToEvent(newAssetDO));
    }

    @Override
    public Page<AssetDO> searchByTagName(String tagName, Integer pageIndex, Integer pageSize) {

        return iAssetRepository.searchByTagName(tagName, pageIndex, pageSize);
    }
}
