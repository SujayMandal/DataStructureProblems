package com.fa.dp.business.validator.bo;

import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
public class DPDashboardParamInfo {
    private String id;
    private String inputFileName;
    private String name;
    private String status;
    private String uploadTimestamp;
    private String fetchedDate;
    private String uploadFlag;
    private DynamicPricingFilePrcsStatus dpFileProcessStatusInfo;
    private DPWeekNProcessStatus dpWeekNFileProcessStatusInfo;
    private int classificationMismatchCount;
    private int duplicateCount;
    private int ssInvestorCount;
    private int unsupportedAssetCount;
    private int failedRealResolOrRealCount;
    private int unsupportedPropertyCount;
    private int processedListCount;
    private int raFailedCount;
    // Week N params
    private int dataFetchFailCount;
    private int unsupportedStateOrZipCount;
    private int ssAndPmiCount;
    private int sopCount;
    private int weekNRAFailedCount;
    private int weekNAssignmentCount;
    private Long totalAssets;
    private int past12CyclesCount;
    private int oddListingsCount;
    private int activeListingsCount;
    private int successUnderreviewCount;
    private DateTime uploadTimeStampInMillis;

    // SOP Week 0 params
    private DPSopWeek0ProcessStatusInfo dpSopWeek0ProcessStatus;
    private int sopWeek0DuplicateAssetCount;
    private int sopWeek0UnsupportedAssetValueCount;
    @Override
    public String toString() {
        return "DPDashboardParamInfo{" +
                "inputFileName='" + inputFileName + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", uploadTimestamp='" + uploadTimestamp + '\'' +
                ", fetchedDate='" + fetchedDate + '\'' +
                ", uploadFlag='" + uploadFlag + '\'' +
                ", classificationMismatchCount=" + classificationMismatchCount +
                ", duplicateCount=" + duplicateCount +
                ", ssInvestorCount=" + ssInvestorCount +
                ", unsupportedAssetCount=" + unsupportedAssetCount +
                ", failedRealResolOrRealCount=" + failedRealResolOrRealCount +
                ", unsupportedPropertyCount=" + unsupportedPropertyCount +
                ", processedListCount=" + processedListCount +
                ", raFailedCount=" + raFailedCount +
                ", dataFetchFailCount=" + dataFetchFailCount +
                ", unsupportedStateOrZipCount=" + unsupportedStateOrZipCount +
                ", ssAndPmiCount=" + ssAndPmiCount +
                ", sopCount=" + sopCount +
                ", weekNRAFailedCount=" + weekNRAFailedCount +
                ", weekNAssignmentCount=" + weekNAssignmentCount +
                ", totalAssets=" + totalAssets +
                ", past12CyclesCount=" + past12CyclesCount +
                ", oddListingsCount=" + oddListingsCount +
                ", uploadTimeStampInMillis=" + uploadTimeStampInMillis +
                ", dpSopWeek0ProcessStatus=" + dpSopWeek0ProcessStatus +
                ", sopWeek0DuplicateAssetCount=" + sopWeek0DuplicateAssetCount +
                ", sopWeek0UnsupportedAssetValueCount=" + sopWeek0UnsupportedAssetValueCount +
                '}';
    }
}
