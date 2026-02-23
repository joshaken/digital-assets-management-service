package co.assets.manage.config;

public interface RocketMqConfig {

    interface Topic {
        String ASSET_TAG_AI = "assetTagAiT-out-0";

    }

    interface Group {
        String ASSET_TAG_AI_CONSUMER = "asset-tag-consumer-group";
    }
}
