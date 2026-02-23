# AI自動タグ付け機能を想定した画像アセット管理API

## 1. プロジェクト概要

本プロジェクトは、「デジタルアセット管理（DAM）」機能を想定し、

- アセット登録
- タグ検索
- AIによる自動タグ付与

を提供する軽量REST APIとして実装したものです。

単なるCRUDではなく、

- 非同期処理設計
- AI外部連携
- 障害耐性設計
- リトライ制御
- 将来的拡張性

を考慮した実務的構成としている。

------

## 2. 使用技術

| 分類         | 技術                                   |
|------------|--------------------------------------|
| 言語         | Java 17                              |
| フレームワーク    | Spring Boot 3.5                      |
| ORM        | Spring Data JPA                      |
| DB         | H2 / MySQL想定                         |
| 非同期        | RocketMQ     / Spring Event          |
| 定期実行       | XXL-Job                              |
| AI連携       | Spring AI （Ollamaのllama3.2-vision利用） |
| HTTPクライアント | OkHttp3                              |
| DTO変換      | MapStruct                            |
| Validation | Spring Validation                    |

------

## 3. アーキテクチャ設計

### 3.1 主要なパッケージ構成

| パッケージ          | 説明                   |
|----------------|----------------------|
| config         | 各種設定                 |
| domain         | ドメインモデル層             |
| service        | 業務ロジック層              |
| infrastructure | 外部連携・永続化層（AI/MQ/DB）  |
| trigger        | 外部I/F層（HTTP・MQ・Task） |

責務分離を明確化し、拡張容易性を確保している。

### 3.2 各レイヤの責務

------

#### domain層

- aggregates … 集約ルート
- po … Entity
- query … 検索専用モデル
- repository … Repositoryインターフェース
- event … ドメインイベント

設計意図：

ドメインロジックをインフラ依存から分離し、
テスト容易性と拡張性を確保。

------

#### service層

- 業務ユースケース実装
- workflowパッケージで業務フロー分離
- IAssetService / ITagServiceでインターフェース抽象化

設計意図：

アプリケーションロジックを集約し、
将来的なマイクロサービス分割を想定。

------

#### infrastructure層

- ai … AI呼び出し実装
- mq … RocketMQ連携
- repository … JPA実装
- storage … 将来的な外部ストレージ拡張

設計意図：

外部システム依存を隔離。

------

#### trigger層

- http … REST Controller
- mq … MQ Consumer
- task … 定期バッチ（XXL-Job）
- Event... イベント消費

設計意図：

システム外部との接点を明確に分離。

------

## 3.3 アセット登録フロー（mqモード）

```mermaid
flowchart TD
    A[Client] --> B[AssetsController]
    B --> C[AssetConverter]
    C --> D[AssetService]
    D --> E[(DB: asset保存)]
    E --> F[ai_tag_status = PENDING]
    D --> G[RocketMQ送信]
    B --> H[即時レスポンス]
    G --> I[AssetTagMqConsumer]
    I --> J[TagService.addTag]
    J --> K{処理成功?}
    K -->|成功| L[(DB: asset_tag保存)]
    L --> M[ai_tag_status = SUCCESS]
    K -->|失敗| N[ai_tag_status = FAILED]
```

## 3.4 AIタグ付与内部処理フロー

```mermaid
flowchart TD
    A[TagService.addTag] --> B[AssetProcessingContext生成]
    B --> C[processImageLoad]
    C --> D{画像読み込み成功?}
    D -->|No| E[context.success = false]
    E --> F[ai_tag_status = FAILED]
    D -->|Yes| G[processTagsByAi]
    G --> H{AI呼び出し成功?}
    H -->|No| I[context.success = false]
    I --> F
    H -->|Yes| J[生成asset_tagリスト]
    J --> K[(DB: batchCreate)]
    K --> L[ai_tag_status = SUCCESS]
```

## 3.5 XXL-Jobリトライフロー

```mermaid
flowchart TD
    A[XXL-Job 5分ごと実行] --> B[FAILEDかつ retry<2 のAssetを取得<br/>最大30件]
    B --> C{ai_tag_status=FAILED<br/>対象データ存在?}
    C -->|No| D[終了]
    C -->|Yes| E[RetryAddTag実行]
    E --> F[AssetProcessingContext.setIncrRetry = TRUE]
    F --> G[assetProcess実行]
    G --> H{処理成功?}
    H -->|成功| I[(DB: asset_tag保存)]
    I --> J[ai_tag_status = SUCCESS]
    H -->|失敗| K[ai_tag_status = FAILED]
    G --> L[retry_count +1]
```

## 3.6 タグ検索フロー

```mermaid
flowchart TD
    A[Client] --> B[AssetsController]
    B --> C[TagConverter]
    C --> D[AssetService]
    D --> E{lastPageMaxId存在?}
    E -->|Yes| F[Keyset Pagination]
    E -->|No| G[Offset Pagination]
    F --> H[(DB検索)]
    G --> H
    H --> I[Entity -> DTO変換]
    I --> J[PageResult返却]
```

# 4. 非同期設計

------

## 4.1 動作モード

タグ付与方式は設定により切替可能。

* event モード（デフォルト）:
  Spring ApplicationEvent による非同期実行。

* mq モード:
  RocketMQ経由で非同期実行。

## 4.2 アセット登録フロー（mqモード）

```mermaid
flowchart TD
    A[Client] --> B[Controller]
    subgraph Controller内部
        B --> C[DB保存: PENDING]
        C --> D[MQ送信]
        B --> E[即時レスポンス]
    end
    D --> F[Consumer]
    F --> G[TagService]
    G --> H{成功?}
    H -->|Yes| I[DB更新: SUCCESS]
    H -->|No| J[DB更新: FAILED]
```

------

## 4.3 リトライ設計

### 初回実行

- retry_countは増加しない
- 失敗時は ai_tag_status を FAILED に更新

------

### 再実行方式（XXL-Job）

FAILED状態のデータを定期バッチで再処理する。

実行条件：

```
WHERE ai_tag_status = 'FAILED'
AND ai_tag_retry_count < 2
AND deleted = false
```

- 5分毎に実行可能
- 最大2回まで再試行
- 再試行時に ai_tag_retry_count を +1 更新
- 成功時は ai_tag_status を SUCCESS に更新

使用インデックス：

```
idx_tag_retry (ai_tag_status, ai_tag_retry_count, deleted)
```

------

# 5. API設計

## 5.1 アセット登録

### Endpoint

```
POST /api/assets
```

### Request Body

```
{
  "title": "sample image",
  "filePath": "/images/sample.jpg"
}
```

### バリデーション

| 項目       | 説明           |
|----------|--------------|
| title    | 必須 / 最大255文字 |
| filePath | 必須 / 最大500文字 |

------

### 処理概要

1. DB保存（ai_tag_status = PENDING）
2. タグ付与イベント送信（設定により切替）
3. 即時レスポンス返却

------

### Response

```
{
  "status": 0,
  "message": "success"
}
```

------

## 5.2 タグ検索

### Endpoint

```
GET /api/assets/search
```

### Query Parameter

| パラメータ         | 説明                         |
|---------------|----------------------------|
| tag           | 必須                         |
| pageIndex     | オプション / デフォルト1             |
| pageSize      | オプション / デフォルト20（最大20）      |
| lastPageMaxId | オプション / Keyset Pagination用 |

------

### ページング設計

- 通常ページング（OFFSETベース）
- 大規模データ対応として lastPageMaxId によるKeyset Paginationも考慮

パフォーマンス劣化を防ぐ設計とした。

------

### Response

```
{
  "status": 0,
  "message": "success",
  "data": {
    "count": 100,
    "pageSize": 20,
    "pageIndex": 1,
    "records": [
      {
        "title": "sample image",
        "filePath": "/images/sample.jpg"
      }
    ]
  }
}
```

# 6. DB設計

## 6.1 テーブル構成

| No | テーブル名     | 論理名      | 概要         |
|----|-----------|----------|------------|
| 1  | asset     | アセット情報   | デジタルアセット管理 |
| 2  | tag       | タグ情報     | タグマスタ管理    |
| 3  | asset_tag | アセットタグ関連 | 多対多関連管理    |

## 6.2 設計方針

- 論理削除採用
- AI状態管理
- リトライ制御
- 複合インデックス設計

## 6.3. テーブル定義

### 6.3.1 asset テーブル

#### 概要

デジタルアセット情報およびAIタグ付与状態を管理するテーブル。

#### カラム定義

| No | カラム名               | 型            | PK | NN | 初期値               | 説明       |
|----|--------------------|--------------|----|----|-------------------|----------|
| 1  | id                 | BIGINT       | ○  | ○  | AUTO_INCREMENT    | アセットID   |
| 2  | title              | VARCHAR(255) |    | ○  |                   | アセットタイトル |
| 3  | file_path          | VARCHAR(500) |    | ○  |                   | ファイル保存パス |
| 4  | ai_tag_status      | VARCHAR(20)  |    | ○  | PENDING           | AIタグ付与状態 |
| 5  | ai_tag_retry_count | INT          |    | ○  | 0                 | リトライ回数   |
| 6  | ai_tag_fail_reason | VARCHAR(500) |    |    | ''                | 失敗理由     |
| 7  | create_time        | TIMESTAMP    |    | ○  | CURRENT_TIMESTAMP | 作成日時     |
| 8  | update_time        | TIMESTAMP    |    |    | NULL              | 更新日時     |
| 9  | delete_time        | TIMESTAMP    |    |    | NULL              | 論理削除日時   |
| 10 | deleted            | BOOLEAN      |    | ○  | FALSE             | 論理削除フラグ  |

------

#### AI状態遷移

| 状態      | 説明     |
|---------|--------|
| PENDING | タグ付与待ち |
| SUCCESS | タグ付与成功 |
| FAILED  | タグ付与失敗 |

------

#### リトライ制御仕様

- FAILED 状態
- ai_tag_retry_count < 指定回数（最大2回）
- 定期バッチ（XXL-Job）により再実行
- 再実行時に ai_tag_retry_count を +1 更新

------

#### インデックス設計

| インデックス名       | カラム                                          | 目的          |
|---------------|----------------------------------------------|-------------|
| idx_tag_retry | (ai_tag_status, ai_tag_retry_count, deleted) | リトライ対象検索高速化 |

------

### 6.3.2 tag テーブル

#### 概要

タグマスタ情報を管理する。

#### カラム定義

| No | カラム名        | 型            | PK | NN | 初期値               | 説明         |
|----|-------------|--------------|----|----|-------------------|------------|
| 1  | id          | BIGINT       | ○  | ○  | AUTO_INCREMENT    | タグID       |
| 2  | name        | VARCHAR(100) |    | ○  |                   | タグ名称（ユニーク） |
| 3  | category    | VARCHAR(100) |    |    | ''                | カテゴリ       |
| 4  | create_time | TIMESTAMP    |    |    | CURRENT_TIMESTAMP | 作成日時       |
| 5  | delete_time | TIMESTAMP    |    |    | NULL              | 論理削除日時     |
| 6  | deleted     | BOOLEAN      |    | ○  | FALSE             | 論理削除フラグ    |

------

#### 設計方針

- name は UNIQUE 制約
- deleted による論理削除管理
- 将来的にカテゴリ別検索拡張可能

------

#### インデックス設計

| インデックス名          | カラム             | 目的      |
|------------------|-----------------|---------|
| idx_name_deleted | (name, deleted) | タグ検索高速化 |

------

### 6.3.3 asset_tag テーブル

#### 概要

Asset と Tag の多対多関係を管理する中間テーブル。

------

#### カラム定義

| No | カラム名             | 型           | PK | NN | 初期値               | 説明               |
|----|------------------|-------------|----|----|-------------------|------------------|
| 1  | id               | BIGINT      | ○  | ○  | AUTO_INCREMENT    | 主キー              |
| 2  | asset_id         | BIGINT      |    | ○  |                   | アセットID           |
| 3  | tag_id           | BIGINT      |    | ○  |                   | タグID             |
| 4  | source           | VARCHAR(20) |    | ○  |                   | タグ付与元（USER / AI） |
| 5  | confidence_score | DOUBLE      |    |    |                   | AI信頼度（0.0〜1.0）   |
| 6  | create_time      | TIMESTAMP   |    | ○  | CURRENT_TIMESTAMP | 作成日時             |
| 7  | delete_time      | TIMESTAMP   |    |    | NULL              | 論理削除日時           |
| 8  | deleted          | BOOLEAN     |    | ○  | FALSE             | 論理削除フラグ          |

------

#### 設計方針

- AIタグとユーザー手動タグを区別するため source を保持
- 論理削除を採用
- UNIQUE制約は現時点では未使用
    - 将来的に一意制約導入を検討可能な構成

------

#### インデックス設計

| インデックス名          | カラム                         | 目的           |
|------------------|-----------------------------|--------------|
| idx_tag_asset_id | (tag_id, asset_id, deleted) | タグ→アセット検索高速化 |
| idx_asset_id     | (asset_id, deleted)         | アセット→タグ検索高速化 |

------

### 6.4 ER関係

```
Asset（1）—（n）Asset_Tag（n）—（1）Tag

```

Asset と Tag は多対多関係。

------

### 6.5. 論理削除設計

本システムでは物理削除は行わず、deleted フラグおよび delete_time により論理削除管理を行う。

検索時は deleted = FALSE を条件に含める。

------

# 7. 実行条件

## 7.1 実行環境

本プロジェクトの基本実行環境は以下の通り。

| 項目       | 内容                |
|----------|-------------------|
| Java     | 17 以上             |
| Maven    | 3.8 以上            |
| メッセージキュー | RocketMQ（利用時のみ必要） |
| 定期タスク    | XXL-Job（利用時のみ必要）  |

## 7.2 デフォルト構成（ローカル実行）

```
config:
  ai:
    method: spring
  tag:
    add: event
```

### 動作内容

- AI連携：Spring AI
- 非同期処理：Spring ApplicationEvent
- RocketMQ：未使用
- 定期リトライ：無効

ローカル単体確認を目的とした最小構成。

## 7.3 本番想定構成

```
config:
  ai:
    method: spring
  tag:
    add: mq
  task: xxl
```

### 動作内容

- AI連携：Spring AI
- 非同期処理：RocketMQ
- リトライ制御：XXL-Job
- DB：MySQL等のRDB想定

スケーラビリティおよび耐障害性を考慮した構成。

------

## 7.4 設定切替項目

本プロジェクトでは、以下の設定により動作方式を切り替えることが可能。

------

### ① AI呼び出し方式

```
config:
  ai:
    method: spring
```

| 設定値    | 説明                   |
|--------|----------------------|
| mock   | モック実装                |
| http   | HTTPクライアント実装         |
| spring | Spring AI経由実装（デフォルト） |

------

### ② タグ付与イベント送信方式

```
config:
  tag:
    add: event
```

| 設定値   | 説明                             |
|-------|--------------------------------|
| event | Spring ApplicationEvent（デフォルト） |
| mq    | RocketMQ                       |

mq を指定した場合、タグ付与処理はRocketMQ経由で非同期実行される。

### ③ 定期タスク実行方式

```
config:
  task: xxl
```

| 設定値 | 説明               |
|-----|------------------|
| 未設定 | 無効       （デフォルト） |
| xxl | XXL-Job利用        |

XXL-Jobを利用する場合は、管理画面でジョブ登録が必要。

------

# 8. 設計上の工夫

- DTOとEntityの責務分離
- MapStructによる型安全な変換処理
- Validationによる入力値検証
- 論理削除設計の採用
- AI処理状態の明確化
- 非同期リトライ機構の設計
- レイヤ分離による保守性向上
- 設定による実行方法を切り替え
- 共通レスポンス構造の統一
- グローバル例外ハンドリングによるエラーハンドリングの一元化
- 外部複数API連携の段階分離設計による障害影響局所化

# 9. 将来拡張

- S3等の外部ストレージ連携対応
- Redisによるキャッシュ最適化
- Elasticsearchによる全文検索機能強化
- テナント分離機構の導入によるマルチテナント対応
- Asset種別管理機能の追加（画像・動画・文書対応）

------

# 10. まとめ

本システムは、

- API即時応答性
- AI外部依存への耐性
- パフォーマンスを考慮したインデックス設計
- 再試行制御
- データ増加対応
- 拡張可能構成

を意識し、実務を想定した設計を行った。

