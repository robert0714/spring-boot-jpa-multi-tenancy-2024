# spring boot jpa 多租戶 基於 資料庫（DataBase）

## 創建表結構使用的是 liquibase ，創建租戶時，自動生成 表結構
## 此方式，是 多連接池。每一個租戶都添加一個資料庫連接池，在 map 中。
## 使用的時候，根據租戶 id 獲取連接池
啟動流程

````

一.創建一個 `molly_master` 的 資料庫。
    這是預設的 資料庫 如果，想修改名稱。需要修改兩個地方
    1. `CustomDataSources` 類中的 TENANT_ID_PREFIX 常量。這是預設生成資料庫的首碼名稱。用於區分項目
    2. `TenantUtils` 類中的 DEFAULT_TENANT_ID 常量。這是預設租戶的資料庫

二.修改 `application-dev.yml` 中的資料庫連接池位址

三. "REST API" 目錄中 Test API.http 測試 api 介面

````

