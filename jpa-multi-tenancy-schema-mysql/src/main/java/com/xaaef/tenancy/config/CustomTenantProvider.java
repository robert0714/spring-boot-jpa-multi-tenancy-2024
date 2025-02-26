package com.xaaef.tenancy.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static com.xaaef.tenancy.config.CustomDataSources.TENANT_ID_PREFIX;
import static com.xaaef.tenancy.util.TenantUtils.DEFAULT_TENANT_ID;

import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER;


/**
 * <p>
 *
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/11/29 15:02
 */


@Slf4j
@Component
@AllArgsConstructor
public class CustomTenantProvider implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

    private final DataSource dataSource;


    @Override
    public Connection getConnection(String tenantId) throws SQLException {
        var conn = getAnyConnection();
        // 切换数据库
        String sql = String.format("USE %s%s", TENANT_ID_PREFIX, tenantId);
        conn.createStatement().execute(sql);
        return conn;
    }


    @Override
    public void releaseConnection(String tenantId, Connection conn) throws SQLException {
        // 切换数据库
        String sql = String.format("USE %s%s", TENANT_ID_PREFIX, DEFAULT_TENANT_ID);
        conn.createStatement().execute(sql);
        conn.close();
    }


    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }


    @Override
    public void releaseAnyConnection(Connection conn) throws SQLException {
        conn.close();
    }


    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }


    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }


    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }


    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MULTI_TENANT_CONNECTION_PROVIDER, this);
      //https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#multitenancy-simplification
//        hibernateProperties.put(MULTI_TENANT, MultiTenancyStrategy.DATABASE);
    }


}
