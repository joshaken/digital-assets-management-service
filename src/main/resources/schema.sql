-- language=h2

-- ===============================
-- 1. asset テーブル
-- ===============================

CREATE TABLE asset
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id      BIGINT                        NULL,
    title              VARCHAR(255)                  NOT NULL,
    file_path          VARCHAR(500)                  NOT NULL,
    ai_tag_status      VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    ai_tag_retry_count INT         DEFAULT 0         NOT NULL,
    create_time        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE asset IS 'デジタルアセット情報を管理するテーブル';
COMMENT ON COLUMN asset.id IS 'アセットID（主キー）';
COMMENT ON COLUMN asset.enterprise_id IS '所属企業ID（マルチテナント対応）';
COMMENT ON COLUMN asset.title IS 'アセットタイトル';
COMMENT ON COLUMN asset.file_path IS 'ファイル保存パス';
COMMENT ON COLUMN asset.ai_tag_status IS 'AIタグ付与状態（PENDING / PROCESSING / COMPLETED / FAILED）';
COMMENT ON COLUMN asset.ai_tag_retry_count IS 'AIタグ付与のリトライ回数';
COMMENT ON COLUMN asset.create_time IS '作成日時';
COMMENT ON COLUMN asset.update_time IS '更新日時';

CREATE INDEX idx_enterprise ON asset (enterprise_id);
CREATE INDEX idx_create_time ON asset (create_time);
CREATE INDEX idx_tag_retry ON asset (ai_tag_status, ai_tag_retry_count);

-- ===============================
-- 2. tag テーブル
-- ===============================

CREATE TABLE tag
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    parent_id   BIGINT       NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tag IS 'タグ情報を管理するテーブル';
COMMENT ON COLUMN tag.id IS 'タグID（主キー）';
COMMENT ON COLUMN tag.name IS 'タグ名称';
COMMENT ON COLUMN tag.parent_id IS '親タグID（階層構造用、NULL可）';
COMMENT ON COLUMN tag.create_time IS '作成日時';

CREATE INDEX idx_parent ON tag (parent_id);

-- ===============================
-- 3. asset_tag テーブル
-- ===============================

CREATE TABLE asset_tag
(
    asset_id         BIGINT      NOT NULL,
    tag_id           BIGINT      NOT NULL,
    source           VARCHAR(20) NOT NULL,
    confidence_score DOUBLE,
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE asset_tag IS 'アセットとタグの関連付けテーブル';
COMMENT ON COLUMN asset_tag.asset_id IS 'アセットID';
COMMENT ON COLUMN asset_tag.tag_id IS 'タグID';
COMMENT ON COLUMN asset_tag.source IS 'タグ付与元（USER / AI）';
COMMENT ON COLUMN asset_tag.confidence_score IS 'AIタグの信頼度（0.0～1.0）';
COMMENT ON COLUMN asset_tag.create_time IS '作成日時';

CREATE INDEX idx_asset_tag_id ON asset_tag (asset_id, tag_id);