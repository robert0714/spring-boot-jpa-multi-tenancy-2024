package com.xaaef.tenancy.config;
 
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils; 

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl; 
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;


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
public class CustomTenantProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> implements HibernatePropertiesCustomizer {

    private final CustomDataSources dataSources;


    @Override
    protected DataSource selectAnyDataSource() {
        return dataSources.getMasterDataSource();
    }


    @Override
    protected DataSource selectDataSource(String tenantId) {
        if (StringUtils.isNotBlank(tenantId)) {
            return dataSources.getDataSource(tenantId);
        } else {
            return dataSources.getMasterDataSource();
        }
    }


    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MULTI_TENANT_CONNECTION_PROVIDER, this);        
        //https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#multitenancy-simplification
//      hibernateProperties.put(MULTI_TENANT, MultiTenancyStrategy.DATABASE);
    }


	 


}
