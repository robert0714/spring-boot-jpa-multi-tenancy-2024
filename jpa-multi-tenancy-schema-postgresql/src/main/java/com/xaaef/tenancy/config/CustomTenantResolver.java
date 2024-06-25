package com.xaaef.tenancy.config;

import com.xaaef.tenancy.util.TenantUtils;
import lombok.AllArgsConstructor; 
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;



@Component
@AllArgsConstructor
public class CustomTenantResolver implements CurrentTenantIdentifierResolver<String>,
        HibernatePropertiesCustomizer {
	
	public static final String DEFAULT_TENANT = "DEFAULT";

    @Override
    public String resolveCurrentTenantIdentifier() { 
        return Objects.requireNonNullElse(TenantUtils.getTenantId(), DEFAULT_TENANT);
    }


    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }


    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }


}
