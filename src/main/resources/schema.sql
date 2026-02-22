-- language=h2

-- ===============================
-- 1. asset テーブル
-- ===============================

CREATE TABLE asset
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    title              VARCHAR(255)                   NOT NULL,
    file_path          VARCHAR(500)                   NOT NULL,
    ai_tag_status      VARCHAR(20)  DEFAULT 'PENDING' NOT NULL,
    ai_tag_retry_count INT          DEFAULT 0         NOT NULL,
    ai_tag_fail_reason VARCHAR(500) DEFAULT '',
    create_time        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP    DEFAULT NULL,
    delete_time        TIMESTAMP    DEFAULT NULL,
    deleted            BOOLEAN                        NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE asset IS 'デジタルアセット情報を管理するテーブル';
COMMENT ON COLUMN asset.id IS 'アセットID（主キー）';
COMMENT ON COLUMN asset.title IS 'アセットタイトル';
COMMENT ON COLUMN asset.file_path IS 'ファイル保存パス';
COMMENT ON COLUMN asset.ai_tag_status IS 'AIタグ付与状態（PENDING/ SUCCESS / FAILED）';
COMMENT ON COLUMN asset.ai_tag_retry_count IS 'AIタグ付与のリトライ回数';
COMMENT ON COLUMN asset.ai_tag_fail_reason IS 'AIタグ付けが失敗する理由';
COMMENT ON COLUMN asset.create_time IS '作成日時';
COMMENT ON COLUMN asset.update_time IS '最終更新日時';
COMMENT ON COLUMN asset.delete_time IS '論理削除日時（削除された場合のみ設定）';
COMMENT ON COLUMN asset.deleted IS '論理削除フラグ（0：有効、1：削除済み）';

CREATE INDEX idx_create_time ON asset (create_time);
CREATE INDEX idx_tag_retry ON asset (ai_tag_status, ai_tag_retry_count, deleted);

-- ===============================
-- 2. tag テーブル
-- ===============================

CREATE TABLE tag
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    category    VARCHAR(100)          DEFAULT '',
    create_time TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    delete_time TIMESTAMP             DEFAULT NULL,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE tag IS 'タグ情報を管理するテーブル';
COMMENT ON COLUMN tag.id IS 'タグID（主キー）';
COMMENT ON COLUMN tag.name IS 'タグ名称';
COMMENT ON COLUMN tag.category IS 'カテゴリ';
COMMENT ON COLUMN tag.create_time IS '作成日時';
COMMENT ON COLUMN tag.delete_time IS '論理削除日時（削除された場合のみ設定）';
COMMENT ON COLUMN tag.deleted IS '論理削除フラグ（0：有効、1：削除済み）';

CREATE INDEX idx_name_deleted ON tag (name, deleted);

-- ===============================
-- 3. asset_tag テーブル
-- ===============================

CREATE TABLE asset_tag
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id         BIGINT      NOT NULL,
    tag_id           BIGINT      NOT NULL,
    source           VARCHAR(20) NOT NULL,
    confidence_score DOUBLE,
    create_time      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    delete_time      TIMESTAMP            DEFAULT NULL,
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE asset_tag IS 'アセットとタグの関連付けテーブル';
COMMENT ON COLUMN asset_tag.id IS 'アセットタグID（主キー）';
COMMENT ON COLUMN asset_tag.asset_id IS 'アセットID';
COMMENT ON COLUMN asset_tag.tag_id IS 'タグID';
COMMENT ON COLUMN asset_tag.source IS 'タグ付与元（USER / AI）';
COMMENT ON COLUMN asset_tag.confidence_score IS 'AIタグの信頼度（0.0～1.0）';
COMMENT ON COLUMN asset_tag.create_time IS '作成日時';
COMMENT ON COLUMN asset_tag.delete_time IS '論理削除日時（削除された場合のみ設定）';
COMMENT ON COLUMN asset_tag.deleted IS '論理削除フラグ（0：有効、1：削除済み）';

-- tag -> asset
-- 这里先使用普通组合索引，暂时不使用 UNIQUE index, 如果加上unique index之后需要增加delete_id字段来避免数据不能逻辑删除
CREATE INDEX idx_tag_asset_id ON asset_tag (tag_id, asset_id, deleted);
CREATE INDEX idx_asset_id ON asset_tag (asset_id, deleted);