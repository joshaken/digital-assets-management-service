package co.assets.manage.service.impl;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.po.AssetTagDO;
import co.assets.manage.domain.model.po.TagDO;
import co.assets.manage.domain.model.aggregates.AssetTagRich;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.domain.repository.IAssetTagRepository;
import co.assets.manage.domain.repository.ITagRepository;
import co.assets.manage.enums.AiTagStatusEnum;
import co.assets.manage.infrastructure.ai.AiTagClient;
import co.assets.manage.infrastructure.storage.ImageQueryClient;
import co.assets.manage.service.ITagService;
import co.assets.manage.service.workflow.AssetProcessingContext;
import co.assets.manage.utils.CustomStringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagServiceImpl implements ITagService {

    @Resource
    private ITagRepository iTagRepository;
    @Resource
    private ImageQueryClient imageQueryClient;
    @Resource
    private AiTagClient aiTagClient;
    @Resource
    private IAssetRepository iAssetRepository;
    @Resource
    private IAssetTagRepository iAssetTagRepository;

    @Override
    public void addTag(Long assetId, String filePath) {
        //获取所有的标签
        Map<String, Long> tagIdMap = queryTagAndIdMap();
        //生成打标签处理流对象
        AssetProcessingContext context = new AssetProcessingContext(assetId, filePath, tagIdMap);
        assetProcess(context);
    }

    /**
     * 简化的责任链模式处理打标签的流程，分别包括读取图片具体数据和调用AI获取对应的标签
     *
     * @param context 责任链上下文
     */
    private void assetProcess(AssetProcessingContext context) {
        Long assetId = context.getAssetId();
        Map<String, Long> tagIdMap = context.getTagIdMap();
        //加载图片信息
        processImageLoad(context);
        //当前步骤是否成功
        if (Boolean.TRUE.equals(context.getSuccess())) {
            //获取标签
            processTagsByAi(context);
            //当前步骤是否成功
            if (Boolean.TRUE.equals(context.getSuccess())) {
                //保存标签和asset的映射关系
                Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
                List<AssetTagDO> assetTagDOList = tagIdMap.entrySet()
                        .stream()
                        //过滤掉不可用标签
                        .filter(allowTag -> context.getTagsConfidenceMap().containsKey(allowTag.getKey()))
                        .map(allowSet -> AssetTagRich.ofAi(assetId, allowSet.getValue(), context.getTagsConfidenceMap().get(allowSet.getKey()), currentTime))
                        .toList();
                iAssetTagRepository.batchCreate(assetTagDOList);
                //更新asset的标签状态
                iAssetRepository.updateTagStatus(assetId, AiTagStatusEnum.SUCCESS, "");
            }
        }
        if (Boolean.FALSE.equals(context.getSuccess())) {
            //更新asset的标签状态
            iAssetRepository.updateTagStatus(assetId, AiTagStatusEnum.FAILED, context.getFailReason());
        }

        if (Boolean.TRUE.equals(context.getIncrRetry())) {
            //增加asset的重试次数
            iAssetRepository.updateRetryCount(assetId);
        }
    }

    /**
     * @param context
     */
    private void processImageLoad(AssetProcessingContext context) {
        try {
            byte[] image = imageQueryClient.getImage(context.getFilePath());
            context.setImage(image);

        } catch (Exception e) {

            log.error("addTag getImage failed {}", e.getMessage(), e);
            context.addFailReason("GetImage failed: " + e.getMessage());
        }
    }

    /**
     * @param context
     */
    private void processTagsByAi(AssetProcessingContext context) {
        try {
            Map<String, Double> tagsConfidenceMap = aiTagClient.identifyTags(context.getImage(), context.getTagIdMap().keySet());

            context.setTagsConfidenceMap(tagsConfidenceMap);

        } catch (Exception e) {
            log.error("addTag identifyTags failed {}", e.getMessage(), e);
            context.addFailReason("IdentifyTags failed: " + e.getMessage());

        }
    }

    private Map<String, Long> queryTagAndIdMap() {
        //获取所有的标签, 这里后面可以改成从缓存中获取所有的标签
        List<TagDO> tagDOList = iTagRepository.findAllNotDeletedTag();
        //获取tagName和tageId的映射map
        return tagDOList.stream()
                .collect(Collectors.toMap(TagDO::getName, TagDO::getId, (existing, replacement) -> existing));
    }

    @Override
    public void retryAddTag(List<AssetDO> assetDOList) {
        //获取所有的标签，后续进行复用，避免重复查询
        Map<String, Long> tagIdMap = queryTagAndIdMap();

        assetDOList.forEach(asset -> {
            AssetProcessingContext context = new AssetProcessingContext(asset.getId(), asset.getFilePath(), tagIdMap);
            //无论是否处理成功，更新重试次数+1
            context.setIncrRetry(Boolean.TRUE);
            assetProcess(context);
        });
    }

}
