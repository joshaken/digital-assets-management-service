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
public class AssetsController extends BaseController {

    @Resource
    private IAssetService assetService;

    /**
     * アセット登録
     *
     * @param createAssetRequest アセット info
     * @return 保存成功可否
     */
    @PostMapping("/assets")
    public Result<Void> create(@Validated @RequestBody CreateAssetRequest createAssetRequest) {
        log.info("api/assets request{}", createAssetRequest);
        //转换成创建类，并设置新增asset时必备的参数
        AssetDO assetDO = AssetConverter.INSTANCE.reqTransToDO(createAssetRequest);
        //从当前用户token中获取企业ID，保证数据不混乱,如果是单企业系统可去除
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
        //转换成对象进行传递查询，通过lastPageMaxId自动判断是否需要使用特殊的分页查询
        AssetsQueryCondition assetsQueryCondition = TagConverter.INSTANCE.transToQueryCondition(queryAssetRequest);
        Page<AssetDO> assetPage = assetService.pageQueryByTagName(assetsQueryCondition);
        //转换成对外暴露的对象
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
