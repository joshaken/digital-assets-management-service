

# AI自動タグ付け機能


---

## 1. 概要

- 画像アセットを登録し、タグ情報を管理する軽量 API
- AI サービスによる自動タグ付与を非同期で実行可能
- 将来的に複数企業（マルチテナント）対応可能
- Spring Boot 標準機能と JPA を活用したモダンな設計

---

## 2. データベース設計



## 3. API 実装

### 3.1 アセット登録

- **エンドポイント**: `POST /api/assets`
- **リクエスト例** (`CreateAssetRequest` record)：

```json
{
  "title": "example.jpg",
  "filePath": "bucket/images/example.jpg"
}
````

* **バリデーション**:

    * title: NotNull
    * filePath: 最大 500 文字、将来的にオブジェクトストレージ形式チェック

* **レスポンス**: 保存結果（成功 / 失敗）

---

### 3.2 タグ検索

* **エンドポイント**: `GET /api/assets/search?tag=xxx`
* **動作**: 指定タグを持つアセットをページングで取得
* **ページングパラメータ**:

    * `pageIndex`
    * `pageSize`

---

## 4. 非同期 AI タグ付与設計

* **目的**: アセット登録後に AI で自動タグ付与を行うが、システム応答性を低下させない
* **設計**:

    * Spring の `@Async` + 独自 ThreadPoolTaskExecutor を使用
    * AssetService 内で、非同期タスクとして AI 連携を実行
    * リトライ:

        * `ai_tag_status` = PROCESSING / FAILED 時に再試行可能
        * `ai_tag_retry_count` で回数管理
    * 外部 AI サービス停止時:

        * 例外キャッチ → 再キューイング
        * ログにエラー情報保存

---

## 5. AI タグ付与の呼び出し

* **入力**: 画像データ（byte[]）、指定タグリスト、固定 prompt
* **方法**:

    * 画像データは byte[] として送信
    * prompt に allowedTags を組み込み、指定タグのみ付与させる
* **レスポンス処理**:

    * Map<String, Double> に変換（タグ名 → 信頼度）
    * Map を元に AssetTagDO を生成し DB 保存

---

## 6. 定時・非同期処理

* **軽量スケジューリング**

    * Spring Boot `@Scheduled` を利用
    * XXL-Job をオプションで組み込み可能
    * ConditionalOnProperty で XXL-Job 有効/無効切り替え
* **非同期スレッドプール**

    * カスタム Executor Bean を定義し、タスク並列実行可能

---

## 7. JPA 実装

* Entity / DO 分離
* `AssetDO`, `TagDO`, `AssetTagDO` を作成
* Repository は Spring Data JPA + Specification / JPQL で実装
* 三表結合のタグ検索も JPQL で対応
* 論理削除 (`deleted`) を考慮したクエリ

---

## 8. 設計上のポイント

* **DO 内メソッド**: 自身の状態更新・ファクトリーメソッドのみ
* **record クラス**: リクエスト DTO に使用、デフォルト値はコンストラクタで設定
* **ページング**: `pageIndex` + `pageSize` 形式
* **可搬性**: XXL-Job 不在でも Spring Scheduled で動作可能
* **AI 呼び出し**: 外部依存を抽象化（Ollama / OpenAI など差し替え可能）

---

## 9. 今後の拡張

* AI モデルの差し替え・プラグイン化
* 企業別のマルチテナント制御強化
* 大規模データ対応（インデックス最適化、ページング改善）

---

## 10. 依存ライブラリ

* Spring Boot 17
* Spring Data JPA
* H2 Database
* Spring Validation (`jakarta.validation`)
* Lombok（オプション）
* Spring Scheduling / Async
* okhttp3（AI 画像送信用）
* MapStruct（DTO ↔ DO 変換用）

