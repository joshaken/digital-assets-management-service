package co.assets.manage.infrastructure.ai;

import co.assets.manage.domain.event.AssetTagEvent;

public interface AssetAddTagPublisher {

    /**
     * Asset保存後、下流処理用のイベントまたはメッセージを送信
     * @param assetTagEvent Assetの主要情報
     */
    void sendCreateAssetEvent(AssetTagEvent assetTagEvent);
}
