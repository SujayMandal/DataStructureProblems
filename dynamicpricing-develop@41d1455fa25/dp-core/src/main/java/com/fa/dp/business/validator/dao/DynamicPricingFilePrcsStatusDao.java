package com.fa.dp.business.validator.dao;

import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yogeshku
 * DPFileProcessStatus Repository
 */
public interface DynamicPricingFilePrcsStatusDao extends JpaRepository<DynamicPricingFilePrcsStatus, String> {

	DynamicPricingFilePrcsStatus findByStatus(String status);

}
