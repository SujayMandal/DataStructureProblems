/**
 * 
 */
package com.fa.dp.business.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fa.dp.business.test.domain.TestEntity;


public interface TestDao extends JpaRepository<TestEntity, String> {


}
