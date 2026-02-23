package co.assets.manage.trigger.http;

import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.query.AssetsQueryCondition;
import co.assets.manage.dto.Result;
import co.assets.manage.dto.req.CreateAssetRequest;
import co.assets.manage.dto.req.QueryAssetRequest;
import co.assets.manage.dto.resp.PageResult;
import co.assets.manage.dto.resp.SearchAssetResponse;
import co.assets.manage.utils.converter.AssetConverter;
import co.assets.manage.service.IAssetService;
import co.assets.manage.utils.converter.TagConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class AssetsController {

    @Resource
    private IAssetService assetService;

    /**
     * アセット登録
     *
     * @param createAssetRequest アセット info
     * @return 保存が成功したかどうか
     */
    @PostMapping("/assets")
    public Result<Void> create(@Validated @RequestBody CreateAssetRequest createAssetRequest) {
        log.info("api/assets request{}", createAssetRequest);
        //作成用クラスに変換し、新規Asset作成時に必要なパラメータを設定
        AssetDO assetDO = AssetConverter.INSTANCE.reqTransToDO(createAssetRequest);
        assetService.create(assetDO);
        return Result.ok();
    }


    /**
     * タグ検索
     *
     * @param queryAssetRequest タグ
     * @return 指定されたタグを持つアセットを 一覧取得
     */
    @GetMapping("/assets/search")
    public Result<PageResult<SearchAssetResponse>> search(QueryAssetRequest queryAssetRequest) {
        log.info("api/assets/search request{}", queryAssetRequest);
        //オブジェクトに変換して伝達・検索し、lastPageMaxIdを使って特殊なページングクエリの必要性を自動判断
        AssetsQueryCondition assetsQueryCondition = TagConverter.INSTANCE.transToQueryCondition(queryAssetRequest);
        Page<AssetDO> assetPage = assetService.pageQueryByTagName(assetsQueryCondition);
        //外部公開用のオブジェクトに変換
        return Result.ok(
                PageResult.page(
                        (int) assetPage.getTotalElements(),
                        queryAssetRequest.pageSize(),
                        queryAssetRequest.pageIndex(),
                        assetPage.getContent().stream().map(AssetConverter.INSTANCE::transToResponse).toList()
                )
        );
    }

}
