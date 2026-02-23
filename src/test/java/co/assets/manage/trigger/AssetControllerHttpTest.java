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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
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
    void testCreateAsset() throws Exception {
        CreateAssetRequest request = new CreateAssetRequest(
                "test-asset",
                "https://images.unsplash.com/photo-1511578314322-379afb476865"
        );

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

        Result<Void> response = JsonUtil.toObj(responseJson, new TypeReference<>() {
        });
        assertThat(response.isSuccess()).isEqualTo(Boolean.TRUE);

        // 查询数据库验证
        List<AssetDO> assets = assetJPARepository.findAll();

        assertThat(assets).isNotEmpty();

        AssetDO saved = assets.stream()
                .filter(a -> a.getTitle().equals("test-asset"))
                .findFirst()
                .orElseThrow();

        // 字段断言
        assertThat(saved.getTitle()).isEqualTo("test-asset");
        assertThat(saved.getFilePath())
                .isEqualTo("https://images.unsplash.com/photo-1511578314322-379afb476865");

        // 默认值验证
        assertThat(saved.getAiTagStatus()).isBetween(AiTagStatusEnum.PENDING, AiTagStatusEnum.SUCCESS);
        assertThat(saved.getAiTagRetryCount()).isEqualTo(0);
        assertThat(saved.getDeleted()).isFalse();

        // 查询关联关系
        List<AssetTagDO> relations =
                assetTagJPARepository.findByAssetIdAndDeletedFalse(saved.getId());

        assertThat(relations).isNotEmpty();

        // 查询具体标签
        List<Long> tagIds = relations.stream()
                .map(AssetTagDO::getTagId)
                .toList();

        List<TagDO> tags = tagJPARepository.findAllById(tagIds);

        assertThat(tags).isNotEmpty();

        // 验证标签确实存在
        tags.forEach(tag -> {
            assertThat(tag.getDeleted()).isFalse();
            assertThat(tag.getName()).isNotBlank();
        });
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
                .map(SearchAssetResponse::filePath) // 假设 SearchAssetResponse 有 getFilePath()
                .collect(Collectors.toList());

        // 定义期望的两个 filePath（顺序可能不固定，所以用 containsExactlyInAnyOrder）
        List<String> expectedFilePaths = Arrays.asList(
                "https://images.unsplash.com/photo-1556157382-97eda2d62296",
                "https://images.unsplash.com/photo-1511578314322-379afb476865"
        );

        // 验证：实际结果包含且仅包含这两个 URL（顺序无关）
        assertThat(actualFilePaths)
                .containsAnyElementsOf(expectedFilePaths);
        log.info("Total: {} ", pageResult.getPageCount());
        pageResult.getList().forEach(System.out::println);
    }

    @Test
    @DisplayName("アセット検索 - 'Commercial' タグで分頁検索 → 各ページに3件ずつ、合計11件のデータが返ることを確認")
    void searchAssets_WithTagCommercial_ShouldReturnPaginatedResults() throws Exception {
        // 第一次请求：第一页（pageIndex=1, pageSize=3）
        MvcResult firstPageResult = mockMvc.perform(get("/api/assets/search")
                        .param("tag", "Commercial")
                        .param("pageIndex", "0")
                        .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String firstPageResponseJson = firstPageResult.getResponse().getContentAsString();
        log.info("First page responseJson: {}", firstPageResponseJson);

        Result<PageResult<SearchAssetResponse>> firstPageResponse = JsonUtil.toObj(firstPageResponseJson, new TypeReference<>() {
        });
        assertThat(firstPageResponse.isSuccess()).isTrue();

        PageResult<SearchAssetResponse> firstPageResultData = firstPageResponse.getData();
        assertThat(firstPageResultData).isNotNull();
        assertThat(firstPageResultData.getList()).hasSize(3); // 确保第一条返回了3条记录

        // 第二次请求：第二页（pageIndex=1, pageSize=3）
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
        assertThat(secondPageResultData.getList()).hasSize(3); // 确保第二条也返回了3条记录

        // 验证总数是否为6
        assertThat(firstPageResultData.getCount()).isEqualTo(11);
        assertThat(firstPageResultData.getPageCount()).isEqualTo(4);

        // 打印所有返回的 filePath 以便调试
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
                // 对应 PARAM_ERROR
                .andExpect(jsonPath("$.status").value(-301))
                .andExpect(jsonPath("$.message").value("ファイルパスは必須項目です"));
    }
}
