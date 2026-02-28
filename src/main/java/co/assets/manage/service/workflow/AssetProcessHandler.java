package co.assets.manage.service.workflow;

import co.assets.manage.enums.AssetProcessStepEnum;

public interface AssetProcessHandler {

    void process(AssetProcessingContext context);

    AssetProcessStepEnum getStep();
}

