package com.fa.dp.business.weekn.dao;

import com.fa.dp.business.weekn.entity.DPWeekNHolidays;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DPWeekNHolidayRepo extends JpaRepository<DPWeekNHolidays, String> {

	DPWeekNHolidays findByHolidayTimestamp(String holidayTimeStamp);
}
