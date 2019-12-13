/**
 * 
 */
package com.fa.dp.core.model.dao;

import java.util.List;

import com.fa.dp.core.model.domain.ModelDetail;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 *
 */
public interface ModelDetailDao extends JpaRepository<ModelDetail, String> {

    public ModelDetail findByNameAndMajorVersionAndMinorVersion(String name, Integer majorVersion, Integer minorVErsion);

    public List<ModelDetail> findByName(String name);

    public List<ModelDetail> findByTenantCode(String tenantCode);
    
    public List<ModelDetail>findBymajorVersionIn(String modelName);
    
    public List<ModelDetail>findByminorVersionIn(String modelName);
}