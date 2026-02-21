package co.assets.manage.domain.complex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AssetTageMultiKey implements Serializable {

    private Long assetId;

    private Long tagId;
}
