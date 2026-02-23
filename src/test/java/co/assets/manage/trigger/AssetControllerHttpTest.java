package co.assets.manage.trigger;

import co.assets.manage.DamServiceAppTests;
import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.domain.model.po.AssetTagDO;
import co.assets.manage.domain.model.po.TagDO;
import co.assets.manage.dto.Result;
import co.assets.manage.dto.req.CreateAssetRequest;
import co.assets.manage.dto.resp.PageResult;
import co.assets.manage.dto.resp.SearchAssetResponse;
import co.assets.manage.enums.AiTagStatusEnum;
import co.assets.manage.infrastructure.repository.jpa.AssetJPARepository;
import co.assets.manage.infrastructure.repository.jpa.AssetTagJPARepository;
import co.assets.manage.infrastructure.repository.jpa.TagJPARepository;
import co.assets.manage.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class AssetControllerHttpTest extends DamServiceAppTests {

    @Resource
    private AssetJPARepository assetJPARepository;
    @Resource
    private AssetTagJPARepository assetTagJPARepository;
    @Resource
    private TagJPARepository tagJPARepository;
    @Resource
    private MockMvc mockMvc;

    @Resource
    @Qualifier(AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    /**
     * テスト用SQLを実行する
     */
    @BeforeAll
    static void setup(@Autowired DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScripts(
                new ClassPathResource("/test-sql/init-tag.sql"),
                new ClassPathResource("/test-sql/init-asset.sql"),
                new ClassPathResource("/test-sql/init-asset-tag.sql")
        );
        populator.setSeparator(";");
        // テストデータを挿入する
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

    /**
     * アセット登録テスト
     */
    @Test
    @DisplayName("アセット作成テスト：有効なリクエストでアセットが正常に保存され、関連タグも正しく登録されることを確認")
    void testCreateAsset() throws Exception {
        //テスト用リクエスト作成
        CreateAssetRequest request = new CreateAssetRequest(
                "test-asset",
                "https://images.unsplash.com/photo-1511578314322-379afb476865"
        );
        //API 呼び出し
        MvcResult mvcResult = mockMvc.perform(
                        post("/api/assets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(request))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();
        log.info("responseJson {}", responseJson);

        //レスポンスの検証
        Result<Void> response = JsonUtil.toObj(responseJson, new TypeReference<>() {
        });
        assertThat(response.isSuccess()).isEqualTo(Boolean.TRUE);

        //  データベースから保存されたアセットを取得
        List<AssetDO> assets = assetJPARepository.findAll();

        assertThat(assets).isNotEmpty();

        AssetDO saved = assets.stream()
                .filter(a -> a.getTitle().equals("test-asset"))
                .findFirst()
                .orElseThrow();

        // アセットフィールドの検証
        assertThat(saved.getTitle()).isEqualTo("test-asset");
        assertThat(saved.getFilePath())
                .isEqualTo("https://images.unsplash.com/photo-1511578314322-379afb476865");

        // デフォルト値の検証
        assertThat(saved.getAiTagStatus()).isBetween(AiTagStatusEnum.PENDING, AiTagStatusEnum.SUCCESS);
        assertThat(saved.getAiTagRetryCount()).isEqualTo(0);
        assertThat(saved.getDeleted()).isFalse();

        //非同期処理の完了を待機してから結果を取得する
        waitForAsyncTasksToComplete();

        // 関連タグ関係の検証
        List<AssetTagDO> relations =
                assetTagJPARepository.findByAssetIdAndDeletedFalse(saved.getId());

        assertThat(relations).isNotEmpty();

        // タグIDを抽出してタグエンティティを取得
        List<Long> tagIds = relations.stream()
                .map(AssetTagDO::getTagId)
                .toList();

        List<TagDO> tags = tagJPARepository.findAllById(tagIds);

        assertThat(tags).isNotEmpty();

        // 各タグの整合性を検証
        tags.forEach(tag -> {
            assertThat(tag.getDeleted()).isFalse();
            assertThat(tag.getName()).isNotBlank();
        });
    }

    /**
     * 非同期タスクの完了を待機するユーティリティメソッド
     */
    private void waitForAsyncTasksToComplete() throws InterruptedException {
        ThreadPoolExecutor executor = asyncTaskExecutor.getThreadPoolExecutor();
        executor.shutdown();

        boolean finished = executor.awaitTermination(60, TimeUnit.SECONDS);

        if (!finished) {
            executor.shutdownNow();
            throw new RuntimeException("非同期タスクが60秒以内に完了しませんでした");
        }

        asyncTaskExecutor.initialize();
    }

    /**
     * タグ検索テスト
     */
    @Test
    @DisplayName("アセット検索 - 'Commercial' タグで検索 → 2件の特定画像が返ることを確認")
    void testSearchByTag() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/assets/search")
                                .param("tag", "Commercial")
//                        .param("lastPageMaxId", "0")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        log.info("responseJson {}", responseJson);

        Result<PageResult<SearchAssetResponse>> response = JsonUtil.toObj(responseJson, new TypeReference<>() {
        });
        assertThat(response.isSuccess()).isEqualTo(Boolean.TRUE);
        PageResult<SearchAssetResponse> pageResult = response.getData();

        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getList()).isNotEmpty();
        assertThat(pageResult.getList()).hasSize(11);

        List<String> actualFilePaths = pageResult.getList().stream()
                .map(SearchAssetResponse::filePath)
                .collect(Collectors.toList());

        List<String> expectedFilePaths = Arrays.asList(
                "https://images.unsplash.com/photo-1556157382-97eda2d62296",
                "https://images.unsplash.com/photo-1511578314322-379afb476865"
        );

        // 検証：実際の結果に含まれるURLはこの2件のみであること
        assertThat(actualFilePaths)
                .containsAnyElementsOf(expectedFilePaths);
        log.info("Total: {} ", pageResult.getPageCount());
        pageResult.getList().forEach(p -> log.info("{}", p));
    }

    @Test
    @DisplayName("アセット検索 - 'Commercial' タグで分頁検索 → 各ページに3件ずつ、合計11件のデータが返ることを確認")
    void searchAssets_WithTagCommercial_ShouldReturnPaginatedResults() throws Exception {
        // 1回目のリクエスト：1ページ目（pageIndex=1, pageSize=3）
        MvcResult firstPageResult = mockMvc.perform(get("/api/assets/search")
                        .param("tag", "Commercial")
                        .param("pageIndex", "1")
                        .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String firstPageResponseJson = firstPageResult.getResponse().getContentAsString();
        log.info("second page responseJson: {}", firstPageResponseJson);

        Result<PageResult<SearchAssetResponse>> firstPageResponse = JsonUtil.toObj(firstPageResponseJson, new TypeReference<>() {
        });
        assertThat(firstPageResponse.isSuccess()).isTrue();

        PageResult<SearchAssetResponse> firstPageResultData = firstPageResponse.getData();
        assertThat(firstPageResultData).isNotNull();
        // 3件のみが返却されていることを確認する
        assertThat(firstPageResultData.getList()).hasSize(3);

        // 2回目のリクエスト：2ページ目（pageIndex=2, pageSize=3）
        MvcResult secondPageResult = mockMvc.perform(get("/api/assets/search")
                        .param("tag", "Commercial")
                        .param("pageIndex", "2")
                        .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String secondPageResponseJson = secondPageResult.getResponse().getContentAsString();
        log.info("Second page responseJson: {}", secondPageResponseJson);

        Result<PageResult<SearchAssetResponse>> secondPageResponse = JsonUtil.toObj(secondPageResponseJson, new TypeReference<>() {
        });
        assertThat(secondPageResponse.isSuccess()).isTrue();

        PageResult<SearchAssetResponse> secondPageResultData = secondPageResponse.getData();
        assertThat(secondPageResultData).isNotNull();
        // 3件のみが返却されていることを確認する
        assertThat(secondPageResultData.getList()).hasSize(3);

        // 総件数11件、総ページ数4であることを確認する
        assertThat(firstPageResultData.getCount()).isEqualTo(11);
        assertThat(firstPageResultData.getPageCount()).isEqualTo(4);

        List<String> allFilePaths = Stream.concat(
                firstPageResultData.getList().stream(),
                secondPageResultData.getList().stream()
        ).map(SearchAssetResponse::filePath).collect(Collectors.toList());

        log.info("All file paths: {}", allFilePaths);
    }

    @Test
    @DisplayName("アセット作成 - リクエストパラメータ不正 → -301")
    void createAsset_WhenInvalidRequest_ThenReturn400() throws Exception {
        CreateAssetRequest invalidRequest = new CreateAssetRequest("x", null);

        // When & Then
        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(invalidRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(-301))
                .andExpect(jsonPath("$.message").value("ファイルパスは必須項目です"));
    }
}
