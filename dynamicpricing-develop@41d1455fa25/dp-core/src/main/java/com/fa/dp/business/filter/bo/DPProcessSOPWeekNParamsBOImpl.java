package com.fa.dp.business.filter.bo;

import com.fa.dp.business.sop.week0.dao.DPSopWeek0ParamsDao;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNParamDao;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.util.RAClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Named
@Slf4j
public class DPProcessSOPWeekNParamsBOImpl  extends AbstractDelegate implements DPProcessSOPWeekNParamsBO{

    @Inject
    private DPSopWeekNParamDao dpSopWeekNParamDao;

    @Inject
    private DPSopWeek0ParamsDao dpSopWeek0ParamsDao;

    @Override
    public DPProcessWeekNParamInfo checkReduction(String selrPropId, String oldLoanNumber, LocalDate currentListEndDate) {
        log.debug("selrPropId : {}, oldLoanNumber : {}, list end date : {}", selrPropId, oldLoanNumber, currentListEndDate);
        DPProcessWeekNParamInfo weeknParamInfo = null;


        long end = currentListEndDate.atStartOfDay(ZoneId.of(RAClientUtil.EST_TIME_ZONE)).toEpochSecond() * 1000;

        DPSopWeekNParam data = null;
        try {
            List<DPSopWeekNParam> dataList = dpSopWeekNParamDao.findByRbidPropIdAndDeliveryDate(selrPropId, oldLoanNumber, end);
            if(CollectionUtils.isNotEmpty(dataList)) {
                if(dataList.size() > 1) {
                    data = dataList.stream().filter(d->d.getFailedStepCommandName() == null).collect(Collectors.toList()).get(0);
                }
                if(data == null) {
                    data = dataList.get(0);
                }
            }
            weeknParamInfo = convert(data, DPProcessWeekNParamInfo.class);
        } catch (Exception e) {
            log.error("Problem in fetching data. {}", e);
        }


        return weeknParamInfo;
    }


    @Override
    public List<DPSopWeek0Param> findLatestNonDuplicateInSopWeek0ForAsset(Set<String> assetFromHbz) {
        List<DPSopWeek0Param> infoData = new ArrayList<>();
        try {
            infoData = dpSopWeek0ParamsDao.findLatestNonDuplicateInSopWeek0ForAsset(assetFromHbz);

        } catch (Exception e) {
            log.error("Problem in getting non duplicate in SOP Week0 for given asset numbers. {}.", e);
        }
        return infoData;
    }
}
