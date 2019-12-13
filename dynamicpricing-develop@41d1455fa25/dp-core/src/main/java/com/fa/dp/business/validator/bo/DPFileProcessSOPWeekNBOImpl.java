package com.fa.dp.business.validator.bo;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.business.weekn.dao.DPSopWeeknProcessStatusRepo;
import com.fa.dp.business.weekn.dao.DPWeekNProcessStatusRepo;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;

@Slf4j
@Named
public class DPFileProcessSOPWeekNBOImpl implements  DPFileProcessSOPWeekNBO{

    @Inject
    private DPSopWeeknProcessStatusRepo dpSOPWeekNProcessStatusRepo;

    @Override
    public LocalDate getLatestWekNRunDate() {
        DPSopWeekNProcessStatus data = dpSOPWeekNProcessStatusRepo.findFirstByOrderByLastModifiedDateDesc();
        return data != null && data.getLastModifiedDate() != null ? LocalDate.ofEpochDay(data.getLastModifiedDate() / 86400000L) : null;
    }

}
