/**
 * 
 */
package com.fa.dp.core.adgroup.dao;

import java.util.List;

import com.fa.dp.core.adgroup.domain.ADGroup;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 *
 */
public interface ADGroupDao extends JpaRepository<ADGroup, String> {

	public ADGroup findByName(String name);

	public List<ADGroup> findByTenantCode(String code);

}
