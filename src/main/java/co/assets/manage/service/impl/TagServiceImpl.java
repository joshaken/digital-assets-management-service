package co.assets.manage.service.impl;

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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        //获取所有的标签, 这里后面可以改成从缓存中获取所有的标签
        List<TagDO> tagDOList = iTagRepository.findAllNotDeletedTag();
        //获取tagName和tageId的映射map
        Map<String, Long> tagIdMap = tagDOList.stream()
                .collect(Collectors.toMap(TagDO::getName, TagDO::getId, (existing, replacement) -> existing));
        //获取图片, 这里应该是访问对象存储获取图像的实体图片
        byte[] image = imageQueryClient.getImage(filePath);

        try {

            //使用AI获取图片的标签
            Map<String, Double> tagsConfidenceMap = aiTagClient.identifyTags(image, tagIdMap.keySet());
            //保存标签关系
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            List<AssetTagDO> assetTagDOList = tagIdMap.entrySet()
                    .stream()
                    .filter(allowTag -> tagsConfidenceMap.containsKey(allowTag.getKey()))
                    .map(allowSet -> AssetTagRich.ofAi(assetId, allowSet.getValue(), tagsConfidenceMap.get(allowSet.getKey()), currentTime))
                    .toList();
            iAssetTagRepository.batchCreate(assetTagDOList);
            //更新asset
            iAssetRepository.updateTagStatus(assetId, AiTagStatusEnum.SUCCESS);
        } catch (Exception e) {
            log.error("addTag failed -> call ai handle exception {}", e.getMessage(), e);
            //保存asset打标签失败，等待后续请求处理
            iAssetRepository.updateTagStatus(assetId, AiTagStatusEnum.FAILED);
        }

    }
}
