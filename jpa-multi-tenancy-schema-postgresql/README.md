# spring boot jpa 多租戶 基於 模式（Schema）

## 創建表結構使用的是 liquibase ，創建租戶時，自動生成 表結構

因為 mysql 資料庫不支援 schema 所以只能使用 PostgreSQL 代替

啟動流程

````

一.創建一個 `molly_master` 的 資料庫。然後創建 `master` 的 schema 
    這是默認的 schema 如果，想修改名稱。需要修改兩個地方
    1. `application.yml`  spring.liquibase.default-schema 屬性。這是預設生成表結構的 schema
    2. `TenantUtils` 類中的 DEFAULT_TENANT_ID 常量

二.修改 `application-dev.yml` 中的資料庫連接池位址

三. "REST API" 目錄中 Test API.http 測試 api 介面

````

