# spring boot jpa 多租戶 基於 資料表（Table）

hibernate 6.x 多租戶注解 @TenantId 。 spring boot 3.x 才支持 hibernate 6.x

啟動流程

````

一.創建一個 `molly_master` 的 資料庫。

二.修改 `application-dev.yml` 中的資料庫連接池位址

三. "REST API" 目錄中 Test API.http 測試 api 介面

````

