package co.assets.manage.service.impl;

import co.assets.manage.domain.model.aggregates.AssetTagRich;
import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.po.AssetTagDO;
import co.assets.manage.domain.model.po.TagDO;
import co.assets.manage.domain.repository.IAssetRepository;
import co.assets.manage.domain.repository.IAssetTagRepository;
import co.assets.manage.domain.repository.ITagRepository;
import co.assets.manage.enums.AiTagStatusEnum;
import co.assets.manage.infrastructure.ai.AiTagClient;
import co.assets.manage.infrastructure.storage.ImageQueryClient;
import co.assets.manage.service.ITagService;
import co.assets.manage.service.workflow.AssetProcessingContext;
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
        //すべてのタグを取得
        Map<String, Long> tagIdMap = queryTagAndIdMap();
        //タグ付け処理フロー用のコンテキストオブジェクトを構築
        AssetProcessingContext context = new AssetProcessingContext(assetId, filePath, tagIdMap);
        assetProcess(context);
    }

    /**
     * 簡易版のチェーン・オブ・レスポンシビリティでタグ付け処理フローを実装。
     * 画像データを読み取るステップと、AIを呼び出して対応するタグを取得するステップの2つを含む。
     * 各ステップで失敗するとチェーンの実行は終了する。
     * 将来的にステップが3つ以上になる場合は、本格的なチェーン・オブ・レスポンシビリティとして抽象化可能。
     *
     * @param context チェーン・オブ・レスポンシビリティ のコンテキスト
     */
    private void assetProcess(AssetProcessingContext context) {
        Long assetId = context.getAssetId();
        Map<String, Long> tagIdMap = context.getTagIdMap();
        //画像情報を読み込む
        processImageLoad(context);
        //現在のステップが成功したかどうか
        if (Boolean.TRUE.equals(context.getSuccess())) {
            //タグを取得
            processTagsByAi(context);
            //現在のステップが成功したかどうか
            if (Boolean.TRUE.equals(context.getSuccess())) {
                //タグとAssetのマッピング関係を保存
                Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
                List<AssetTagDO> assetTagDOList = tagIdMap.entrySet()
                        .stream()
                        //利用できないタグをフィルタリング
                        .filter(allowTag -> context.getTagsConfidenceMap().containsKey(allowTag.getKey()))
                        .map(allowSet ->
                                //タグとAssetのマッピングクラスに変換
                                AssetTagRich.ofAi(assetId, allowSet.getValue()
                                        , context.getTagsConfidenceMap().get(allowSet.getKey())
                                        , currentTime))
                        .toList();
                iAssetTagRepository.batchCreate(assetTagDOList);
                //Assetのタグ付けステータスを更新
                iAssetRepository.updateTagStatus(assetId, AiTagStatusEnum.SUCCESS, "");
            }
        }
        if (Boolean.FALSE.equals(context.getSuccess())) {
            //Assetのタグ付けステータスを失敗に更新
            iAssetRepository.updateTagStatus(assetId, AiTagStatusEnum.FAILED, context.getFailReason());
        }

        if (Boolean.TRUE.equals(context.getIncrRetry())) {
            //Assetのタグ付けリトライ回数を増加
            iAssetRepository.updateRetryCount(assetId);
        }
    }

    /**
     * 画像情報を読み込む process
     *
     * @param context 　チェーン・オブ・レスポンシビリティ のコンテキスト
     */
    private void processImageLoad(AssetProcessingContext context) {
        try {
            byte[] image = imageQueryClient.getImage(context.getFilePath());
            context.setImage(image);

        } catch (Exception e) {

            log.error("TagServiceImpl processImageLoad failed {}", e.getMessage(), e);
            context.addFailReason("GetImage failed: " + e.getMessage());
        }
    }

    /**
     * 外部AIサービスを呼び出して画像のタグを取得
     *
     * @param context 　チェーン・オブ・レスポンシビリティ のコンテキスト
     */
    private void processTagsByAi(AssetProcessingContext context) {
        try {
            Map<String, Double> tagsConfidenceMap = aiTagClient.identifyTags(
                    context.getImage()
                    , context.getTagIdMap().keySet());

            context.setTagsConfidenceMap(tagsConfidenceMap);

        } catch (Exception e) {
            log.error("TagServiceImpl processTagsByAi failed {}", e.getMessage(), e);
            context.addFailReason("IdentifyTags failed: " + e.getMessage());

        }
    }

    private Map<String, Long> queryTagAndIdMap() {
        //すべてのタグを取得。将来的にはキャッシュから取得するように変更可能
        List<TagDO> tagDOList = iTagRepository.findAllNotDeletedTag();

        log.info("Find all undeleted tag count [{}]", tagDOList.size());
        //tagNameとtagIdのマッピングMapを作成
        return tagDOList.stream()
                .collect(Collectors.toMap(TagDO::getName, TagDO::getId, (existing, replacement) -> existing));
    }

    @Override
    public void retryAddTag(List<AssetDO> assetDOList) {
        //すべてのタグを取得し、後で再利用して重複クエリを避ける
        Map<String, Long> tagIdMap = queryTagAndIdMap();

        assetDOList.forEach(asset -> {
            //例外をキャッチして、後続のAsset処理がブロックされないようにする
            try {
                AssetProcessingContext context = new AssetProcessingContext(asset.getId(), asset.getFilePath(), tagIdMap);
                //処理の成否にかかわらず、リトライ回数を+1更新
                context.setIncrRetry(Boolean.TRUE);
                assetProcess(context);
            } catch (Exception e) {
                log.info("retryAddTag AssetId[{}] AssetFilePath[{}] process failed {}", asset.getId(), asset.getFilePath(), e.getMessage());
            }
        });
    }

}
