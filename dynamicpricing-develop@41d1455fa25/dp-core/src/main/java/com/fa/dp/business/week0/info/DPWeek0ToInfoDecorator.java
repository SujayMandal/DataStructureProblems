package com.fa.dp.business.week0.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.input.info.DPAssetDetails;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class DPWeek0ToInfoDecorator implements DPWeek0ToInfoMapper {

    @Inject
    private DPWeek0ToInfoMapper dpWeekToInfoMapper;

    @Override
    public DPProcessParamInfo dpWeek0ToInfoMapper(DPProcessParam dpProcessParam) {
        DPProcessParamInfo dpProcessParamInfo = dpWeekToInfoMapper.dpWeek0ToInfoMapper(dpProcessParam);
        return dpProcessParamInfo;
    }

    @Override
    public DPAssetDetails dpProcessParamToAssetDetailsMapper(DPProcessParam dpProcessParam) {
        return dpWeekToInfoMapper.dpProcessParamToAssetDetailsMapper(dpProcessParam);
    }

    @Override
    public DPFileProcessStatusInfo dpFileProcessStatusToInfo(DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus) {
        DPFileProcessStatusInfo dpFileProcessStatusInfo = dpWeekToInfoMapper.dpFileProcessStatusToInfo(dynamicPricingFilePrcsStatus);
        return dpFileProcessStatusInfo;
    }

    @Override
    public DynamicPricingFilePrcsStatus dpFileProcessStsInfoToStatus(DPFileProcessStatusInfo dpFileProcessStatusInfo) {
        DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus = dpWeekToInfoMapper.dpFileProcessStsInfoToStatus(dpFileProcessStatusInfo);
        return dynamicPricingFilePrcsStatus;
    }
    @Override
    public CommandInfo map(Command command) {
        CommandInfo commandInfo = dpWeekToInfoMapper.map(command);
        return commandInfo;
    }

    @Override
    public DateTime map(Long aLong) {
        return dpWeekToInfoMapper.map(aLong);
    }

}
