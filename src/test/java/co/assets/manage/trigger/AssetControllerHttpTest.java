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
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class AssetControllerHttpTest extends DamServiceAppTests {

//    @LocalServerPort
//    private int port;

    @Resource
    private AssetJPARepository assetJPARepository;
    @Resource
    private AssetTagJPARepository assetTagJPARepository;
    @Resource
    private TagJPARepository tagJPARepository;


    @Resource
    private MockMvc mockMvc;
//    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + 8080 + "/api";
    }

    /**
     * アセット登録テスト
     */
    @Test
    @Sql("/test-sql/init-tag.sql")
    @Sql("/test-sql/init-asset.sql")
    @Sql("/test-sql/init-asset-tag.sql")
    void testCreateAsset() throws Exception {
        CreateAssetRequest request = new CreateAssetRequest(
                "test-asset",
                "https://images.unsplash.com/photo-1511578314322-379afb476865"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateAssetRequest> entity =
                new HttpEntity<>(request, headers);

        MvcResult mvcResult = mockMvc.perform(
                        post(baseUrl() + "/assets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(request))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();

        log.info("responseJson {}", responseJson);

//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();

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
    @Sql("/test-sql/init-tag.sql")
    @Sql("/test-sql/init-asset.sql")
    @Sql("/test-sql/init-asset-tag.sql")
    void testSearchByTag() throws Exception {

//        String url = baseUrl() +
//                "/assets/search?tag=Commercial";
        MvcResult mvcResult = mockMvc.perform(get("/api/assets/search")
                        .param("tag", "Commercial")
//                        .param("lastPageMaxId", "0")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
//        ResponseEntity<Result<PageResult<SearchAssetResponse>>> response =
//                restTemplate.exchange(
//                        url,
//                        HttpMethod.GET,
//                        null,
//                        new ParameterizedTypeReference<Result<PageResult<SearchAssetResponse>>>() {
//                        }
//                );

        String responseJson = mvcResult.getResponse().getContentAsString();
        log.info("responseJson {}", responseJson);

        Result<PageResult<SearchAssetResponse>> response = JsonUtil.toObj(responseJson, new TypeReference<Result<PageResult<SearchAssetResponse>>>() {
        });
        assertThat(response.isSuccess()).isEqualTo(Boolean.TRUE);
        PageResult<SearchAssetResponse> pageResult = response.getData();

        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getList()).isNotEmpty();

        log.info("Total: {} ", pageResult.getPageCount());
        pageResult.getList().forEach(System.out::println);
    }
}
