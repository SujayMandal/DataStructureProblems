package com.fa.dp.business.weekn.input.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.core.util.DateConversionUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class DPWeekNToInfoDecorator implements DPWeekNToInfoMapper {

    @Inject
    private DPWeekNToInfoMapper dpWeekNToInfoMapper;

    @Override
    public DPProcessWeekNParamInfo dpWeekNToInfoMapper(DPProcessWeekNParam dpProcessWeekNParam) {
        DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(dpProcessWeekNParam);
        dpProcessWeekNParamInfo.getDpWeekNProcessStatus().setFetchedDateStr(DateConversionUtil.getEstDate(dpProcessWeekNParam.getDpWeekNProcessStatus().getFetchedDate()).toString(DateConversionUtil.DATE_TIME_FORMATTER));
        DateTime parsedDate = DateConversionUtil.EST_DATE_TIME_FORMATTER.parseDateTime(DateConversionUtil.getEstDate(dpProcessWeekNParam.getDpWeekNProcessStatus().getLastModifiedDate()).toString(DateConversionUtil.DATE_TIME_FORMATTER));
        dpProcessWeekNParamInfo.getDpWeekNProcessStatus().setLastModifiedDate(DateConversionUtil.getEstDate(parsedDate.getMillis()));
        return dpProcessWeekNParamInfo;
    }

    @Override
    public DPWeekNProcessStatusInfo map(DPWeekNProcessStatus dpWeekNProcessStatus) {
        DPWeekNProcessStatusInfo dpWeekNProcessStatusInfo = dpWeekNToInfoMapper.map(dpWeekNProcessStatus);
        return dpWeekNProcessStatusInfo;
    }

    @Override
    public CommandInfo map(Command command) {
        CommandInfo commandInfo = dpWeekNToInfoMapper.map(command);
        return commandInfo;
    }

    @Override
    public DateTime map(Long aLong) {
        return dpWeekNToInfoMapper.map(aLong);
    }


}
