/**
 * 
 */
package com.fa.dp.core.systemparam.dao;

import com.fa.dp.core.systemparam.domain.SystemParameter;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 *
 */
public interface SystemParameterDao extends JpaRepository<SystemParameter, String> {

    public SystemParameter findByKey(String key);

}
