# spring boot jpa 多租戶

* 分析差異表  
                                             
  | diff                 | jpa-multi-tenancy-database | jpa-multi-tenancy-schema-mysql | jpa-multi-tenancy-schema-postgresql | jpa-multi-tenancy-table |
  |----------------------|----------------------------|--------------------------------|-------------------------------------|-------------------------|
  | model| bridge           | bridge             | bridge                                   | pool |
  | <font color="red">連線池</font>|每個租戶有專屬連線池 | 要測試才會知道是<br/>單一連線池，要在池中者不同租戶的連線嗎? |     | 共用 |
  | entities| 無@TenantId   | 無@TenantId| 無@TenantId| 使用hibernate 6.x 多租户@TenantId |
  | CustomTenantProvider | AbstractDataSourceBased-<br/>MultiTenant-<br/>ConnectionProviderImpl<br/>呼叫CustomDataSources的Map進行dtasource切換| MultiTenant-<br/>ConnectionProvider<br/>提供使用SQL對既有連線切換資料庫   |                                    |無 |
  | spring-boot version | 2.7.x | 3.0.x<br/>https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#multitenancy-simplification     | 3.0.x                                   |無 |  
  | CustomDataSources<br/>並未實作公定版本Datasource | Map多組datasource  | hardcode更換jdbc的url           | 2                                   |無 | 
  | TenantServiceImpl  | 管理租戶對應設定資料生成 與初始化資料庫表格  |管理租戶對應設定資料生成 與初始化資料庫表格  |管理租戶對應設定資料生成 與初始化資料庫表格     |管理租戶對應設定資料生成 與初始化資料庫表格 |
  | TenantIdInterceptor  | 檢驗header中有無``X_TENANT_ID``   |檢驗header中有無``X_TENANT_ID``   |檢驗header中有無``X_TENANT_ID``                           |檢驗header中有無``X_TENANT_ID`` |
  | TenantTableRunner    | 初始化新租戶的table結構   |初始化新租戶的table結構  | 2                     | 無  |
  | jdbc driver    | mysql-connector-j   |mysql-connector-j|<font color="red">postgresql</font> |mysql-connector-j|
  | TenantController|租戶管理(新增&列表)|租戶管理(新增&列表)|租戶管理(新增&列表)|租戶管理(新增&列表)|
  | 關鍵   | 資料庫molly_master的tenant表格產生其他多租戶資料庫||||


## jpa-multi-tenancy-database                [資料庫（DataBase）]

## jpa-multi-tenancy-schema-postgresql       [模式（Schema）]

## jpa-multi-tenancy-schema-mysql            [模式（Schema）]

## jpa-multi-tenancy-table            [資料表（Table）]


# Other References
* 從JPA實作Hibernate下手: MultiTenantConnectionProvider的思路
  * 2023.05.19 Thomas Vitale於Spring I/O 2023的演講
    * Video: https://www.youtube.com/watch?v=pG-NinTx4O4
      * 時間[10:17](https://youtu.be/pG-NinTx4O4?t=617)所示範的TenantController並沒有在github中
    * Slides: https://speakerdeck.com/thomasvitale/multitenant-mystery-only-rockers-in-the-building
       * slides所提供其他資源連結:
         * How to integrate Hibernates Multitenant feature with Spring Data JPA in a Spring Boot application
            * https://spring.io/blog/2022/07/31/how-to-integrate-hibernates-multitenant-feature-with-spring-data-jpa-in-a-spring-boot-application
         * Multitenancy in Hibernate
            * https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#multitenacy
         * Multitenancy OAuth2 with Spring Security
            * https://www.youtube.com/watch?v=ke13w8nab-k
         * Context Propagation with Project Reactor 3
            * https://spring.io/blog/2023/03/28/context-propagation-with-project-reactor-1-the-basics
         * Creating a custom Spring Cloud Gateway Filter 
            * https://spring.io/blog/2022/08/26/creating-a-custom-spring-cloud-gateway-filter
         * Multitenancy with Spring Data JDBC
            * https://spring.io/blog/2022/03/23/spring-tips-multitenant-jdbc
    * GitHub repo: 
       * 草稿: https://github.com/ThomasVitale/multitenant-spring-boot-demo
       * 完整版本： https://github.com/ThomasVitale/spring-boot-multitenancy
         * 事前準備
           * flyway
             * https://www.baeldung.com/database-migrations-with-flyway
             * https://www.baeldung.com/spring-boot-flyway-repair
           * parameters 
             * https://docs.spring.io/spring-boot/appendix/application-properties/index.html
             * 2024.0619 test env              
               * 發現有去打restful 4318 port ...需要注意的是Thomas是在Mac上寫的程式，沒有考慮在windows上需要調整。也就是windows 上執行容器未必可以正常轉port (我的筆電正常，家用電腦異常)
               ```bash
                 java -jar instrument-service-0.0.1-SNAPSHOT.jar 
                 --spring.datasource.url=jdbc:postgresql://172.18.204.152:5432/grafana 
                 --spring.datasource.username=user 
                 --spring.datasource.password=password  
               ```
               
               * Now open the browser window and navigate to `http://dukes.rock/instruments/`. You'll be redirected to the Keycloak authentication page. Log in with `isabelle/password`. The result will be the list of instruments from the Dukes rock band.
               * Now open another browser window and navigate to `http://beans.rock/instruments/`. You'll be redirected to the Keycloak authentication page. Log in with `bjorn/password`. The result will be the list of instruments from the Beans rock band.
               * test path
               ```bash
                 http://localhost:8181/instruments
                 http://dukes.rock/actuator
                 http://beans.rock/actuator 
                 http://dukes.rock/instruments
                 http://beans.rock/instruments
                 http://localhost:12345/
                 http://localhost:3000/dashboards
                 http://localhost:4318/
               ```
    * keywords:
      * TenantContext 
        * 在完整版的程式碼中，重構為``com.thomasvitale.instrumentservice.multitenancy.context.TenantContextHolder``
      * [MVC configuration](https://youtu.be/pG-NinTx4O4?t=582):
        * First,  where's the resolver I need to mark this as a component . so spring will find it and then I need to provide the web configuration so I can implement the web MVC configurer that provides a method to add [interceptors to the application so I can say intercept registry my tenant interceptor](https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/web/WebConfig.java#L18).
        * All right ,so at this point every request if I provide that HTTP header will know what is the current tenant .so I can just to test it out .I can define a rest controller to return the current tenant .
        * And in this method now and anywhere else as part of the processing with the request I can say tenant context dot get standard all right let's verify that now everything is working correctly before moving on the application is running it's running on Port 8181 
          ```java
          package com.thomasvitale.instrumentservice.instrument.web;

          import com.thomasvitale.instrumentservice.multitenancy.context.TenantContext;
          import org.springframework.web.bind.annotation.GetMapping;
          import org.springframework.web.bind.annotation.RequestMapping;
          import org.springframework.web.bind.annotation.RestController;

          @RestController
          @RequestMapping("tenant")
          public class TenantController {
              @GetMapping
              String getTenant(){
                  return TenantContext.getTenantId();
              }
          }
          ```


           
      * getConnection: https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/data/hibernate/ConnectionProvider.java#L36-L39
      * releaseConnection: https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/data/hibernate/ConnectionProvider.java#L43-L46
      * TenantInterceptor: https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/web/TenantInterceptor.java#L22-L26
    * **Arconia framework**: 
      * ThomasVitale 發展中的SaaS framework，沒有交代tx multi-tenant，所以不納入討論。
      * Description: Arconia is a framework to build SaaS, multitenant applications using Java and Spring Boot.
      * Github: https://github.com/arconia-io/arconia