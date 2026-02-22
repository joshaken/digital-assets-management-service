package co.assets.manage.service.impl;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.domain.repository.ITagRepository;
import co.assets.manage.infrastructure.ai.AssetAddTagPublisher;
import co.assets.manage.service.IAssetService;
import co.assets.manage.utils.converter.AssetConverter;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AssetServiceImpl implements IAssetService {
    @Resource
    private IAssetRepository iAssetRepository;
    @Resource
    private AssetAddTagPublisher assetAddTagPublisher;
    @Resource
    private ITagRepository iTagRepository;

    @Override
    public void create(AssetDO createAssetRequest) {
        //保存数据库
        AssetDO newAssetDO = iAssetRepository.save(createAssetRequest);
        //异步调用第三方AI，这里使用一个通用接口，后续可以改成发送MQ消息，在消费方再进行外部AI接口调用
        assetAddTagPublisher.sendCreateAssetEvent(AssetConverter.INSTANCE.transToEvent(newAssetDO));
    }

    @Override
    public Page<AssetDO> pageQueryByTagName(AssetsQueryCondition queryCondition) {

        //如果未传递lastPageMaxId,需要直接使用简单的联表查询，主要注意SQL是否有走索引
        if (Objects.isNull(queryCondition.getLastPageMaxId())) {
            return iAssetRepository.pageQueryByTagName(queryCondition.getTag(), queryCondition.getPageIndex(), queryCondition.getPageSize());
        }
        //如果未传递lastPageMaxId，代码项目数据变大，需要对分页查询做额外处理
        //分页查询，先拿到标签对应的标签ID，这里可以改成前端请求中直接传递tagId而不是tagName
        Long tagId = iTagRepository.findTagIdByName(queryCondition.getTag());
        if (tagId == null) {
            return Page.empty();
        }
        queryCondition.setTagId(tagId);
        //通过 > 上一页最大ID limit 页数获取数据
        List<AssetDO> assetDOList = iAssetRepository.findAssetByMinId(queryCondition);
        //单独获取总条数数据,这里的总页数，后面可以做成从Redis缓存中获取
        Long count = iAssetRepository.countByTagId(queryCondition.getTagId());
        return new PageImpl<>(assetDOList
                , PageRequest.of(queryCondition.getPageIndex(), queryCondition.getPageSize())
                , count);

    }
}
