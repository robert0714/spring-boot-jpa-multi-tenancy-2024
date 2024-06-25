package com.xaaef.tenancy.service.impl;

import com.xaaef.tenancy.entity.Tenant;
import com.xaaef.tenancy.repository.TenantRepository;
import com.xaaef.tenancy.service.TenantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantReps;


    @Override
    public Tenant save(Tenant entity) {
        if (tenantReps.existsById(entity.getTenantId())) {
            var err = String.format("Tenant ID %s already exists！", entity.getTenantId());
            throw new RuntimeException(err);
        }
        return tenantReps.save(entity);
    }


    @Override
    public List<Tenant> findAll() {
        return tenantReps.findAll();
    }


}
