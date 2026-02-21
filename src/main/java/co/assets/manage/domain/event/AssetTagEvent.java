package co.assets.manage.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AssetTagEvent {

    private Long assetId;

    private String filePath;

}
