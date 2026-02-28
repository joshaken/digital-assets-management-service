package co.assets.manage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssetProcessStepEnum {

    OSS_FETCH(1),
    AI_TAG(2),
    UPDATE_ASSET(3),
    ;

    private final Integer step;

}
