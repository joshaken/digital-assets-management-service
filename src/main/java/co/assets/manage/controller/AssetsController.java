package co.assets.manage.controller;

import co.assets.manage.dto.Result;
import co.assets.manage.dto.req.CreateAssetRequest;
import co.assets.manage.dto.req.QueryAssetRequest;
import co.assets.manage.dto.resp.PageResult;
import co.assets.manage.dto.resp.QueryAssetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class AssetsController {

    /**
     * アセット登録
     *
     * @param createAssetRequest アセット info
     * @return 保存成功可否
     */
    @PostMapping("/assets")
    public Result<Void> create(@Validated @RequestBody CreateAssetRequest createAssetRequest) {

        return Result.ok();
    }

    /**
     * タグ検索
     *
     * @param queryAssetRequest タグ
     * @return 指定されたタグを持つアセットを 一覧取得
     */
    @GetMapping("/assets/search")
    public Result<PageResult<QueryAssetResponse>> search(QueryAssetRequest queryAssetRequest) {

        return Result.ok();
    }

}
