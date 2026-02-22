package co.assets.manage.utils.converter;

import co.assets.manage.domain.event.AssetTagEvent;
import co.assets.manage.domain.model.po.AssetDO;
import co.assets.manage.dto.req.CreateAssetRequest;
import co.assets.manage.dto.resp.QueryAssetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface AssetConverter {
    AssetConverter INSTANCE = Mappers.getMapper(AssetConverter.class);

    @Mappings({
            @Mapping(target = "createTime", expression = "java(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()))"),
            @Mapping(target = "aiTagRetryCount", constant = "1"),
            @Mapping(target = "deleted", constant = "false"),
            @Mapping(target = "aiTagStatus", expression = "java(co.assets.manage.enums.AiTagStatusEnum.PENDING)"),
    })
    AssetDO reqTransToDO(CreateAssetRequest createAssetRequest);

    @Mappings({
            @Mapping(target = "assetId", source = "id")
    })
    AssetTagEvent transToEvent(AssetDO assetDO);

    QueryAssetResponse transToResponse(AssetDO assetDO);
}
