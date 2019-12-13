package com.fa.dp.business.weekn.report.delegate;

import com.fa.dp.business.filter.bo.DPProcessSOPWeekNParamsBO;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.core.util.DateConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named
@Slf4j
public class SOPWeeknDailyQAReportDelegateImpl  implements  SOPWeeknDailyQAReportDelegate{


    @Value("${SOP_WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
    private int listSplitCount;


    @Inject
    private DPProcessSOPWeekNParamsBO dpProcessSOPWeekNParamsBO;

    @Override
    public void prepareAssignmentDate(HubzuDBResponse hubzuinfo, Map<String, String> migrationPropToLoanMap) {
        List<DPSopWeek0Param> consolidatedListOfweek0Params = new ArrayList<>();

        List<List<HubzuInfo>> splitListHubzuInfos = ListUtils.partition(hubzuinfo.getHubzuInfos(), listSplitCount);

        splitListHubzuInfos.stream().forEach(listOfBatch -> {
            Set<String> propsFromHbz = listOfBatch.stream().map(HubzuInfo::getSelrPropIdVcNn).collect(Collectors.toSet());
            Set<String> assetFromHbz = propsFromHbz.stream().map(prop -> migrationPropToLoanMap.get(prop)).collect(Collectors.toSet());

            List<DPSopWeek0Param> week0DBList = dpProcessSOPWeekNParamsBO.findLatestNonDuplicateInSopWeek0ForAsset(assetFromHbz);
            if (!week0DBList.isEmpty()) {
                consolidatedListOfweek0Params.addAll(week0DBList);
            }
        });

        Map<String, DPSopWeek0Param> assetValueMap = consolidatedListOfweek0Params.stream()
                .collect(Collectors.toMap(DPSopWeek0Param::getPropTemp, Function.identity(), (r, s) -> r));

        hubzuinfo.getHubzuInfos().stream().forEach(item -> {
            if (assetValueMap.containsKey(item.getSelrPropIdVcNn()) && assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate() != null) {
                DateTime assignmentDate = DateConversionUtil.getEstDate(assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate());
                item.setAssignmentDate(assignmentDate.toDate());
            }
        });
    }
}
