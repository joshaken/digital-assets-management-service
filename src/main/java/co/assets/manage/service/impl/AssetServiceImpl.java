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
import org.springframework.data.domain.Pageable;
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
        //ここでは直接データベースに保存しているが、実際にはユーザーが重複した素材をアップロードする可能性があるため、ロジックによる検証が必要
        AssetDO newAssetDO = iAssetRepository.save(createAssetRequest);
        //サードパーティAIを非同期で呼び出す。ここでは汎用インターフェースを使用しているが、
        //将来的にはMQメッセージを送信し、コンシューマ側で外部AIインターフェースを呼び出すように変更可能
        assetAddTagPublisher.sendCreateAssetEvent(AssetConverter.INSTANCE.transToEvent(newAssetDO));
    }

    @Override
    public Page<AssetDO> pageQueryByTagName(AssetsQueryCondition queryCondition) {

        //まずタグに対応するタグIDを取得する。ここは、フロントエンドからtagNameではなく直接tagIdを渡すように変更可能
        Long tagId = iTagRepository.findTagIdByName(queryCondition.getTag());
        if (tagId == null) {
            return Page.empty();
        }
        queryCondition.setTagId(tagId);
        List<AssetDO> assetDOList;
        //lastPageMaxIdが渡されていない場合
        if (Objects.isNull(queryCondition.getLastPageMaxId())) {
            assetDOList = iAssetRepository.findAssetByTagId(queryCondition);
        } else {
            //lastPageMaxIdが渡されている場合, 前ページの最大IDとlimitを使ってデータを取得
            assetDOList = iAssetRepository.findAssetByMinId(queryCondition);
        }
        // 総件数を個別に取得。
        // ここでの総ページ数は、後でRedisキャッシュから取得するように変更可能
        Long count = iAssetRepository.countByTagId(queryCondition.getTagId());
        return new PageImpl<>(assetDOList
                , Pageable.ofSize(queryCondition.getPageSize())
                , count);

    }
}
