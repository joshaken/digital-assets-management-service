package co.assets.manage.service.workflow;

import co.assets.manage.utils.CustomStringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class AssetProcessingContext {
    private Boolean success;
    private Boolean incrRetry;
    private String failReason;
    private byte[] image;
    private Map<String, Double> tagsConfidenceMap;
    private Map<String, Long> tagIdMap;
    private Long assetId;
    private String filePath;


    public AssetProcessingContext(Long assetId, String filePath, Map<String, Long> tagIdMap) {
        //默认成功
        this.success = Boolean.TRUE;
        this.assetId = assetId;
        this.filePath = filePath;
        this.incrRetry = Boolean.FALSE;
        this.tagIdMap = tagIdMap;
        this.failReason = "";
    }

    public void addFailReason(String reason) {
        this.failReason = CustomStringUtils.truncateToFailReasonMaxLen(reason);
        // 有失败原因就标记为失败
        this.success = Boolean.FALSE;
    }
}
