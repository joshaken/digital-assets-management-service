package co.assets.manage.utils.converter;

import co.assets.manage.domain.model.query.AssetsQueryCondition;
import co.assets.manage.dto.req.QueryAssetRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TagConverter {
    TagConverter INSTANCE = Mappers.getMapper(TagConverter.class);

    AssetsQueryCondition transToQueryCondition(QueryAssetRequest assetRequest);



}
