package com.fa.dp.business.sop.validator.dao;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author misprakh
 */
public interface DPSopWeek0ProcessStatusDao extends JpaRepository<DPSopWeek0ProcessStatus, String> {

	DPSopWeek0ProcessStatus findByStatus(String fileStatus);
}
