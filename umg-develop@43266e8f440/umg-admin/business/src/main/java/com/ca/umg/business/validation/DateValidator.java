package com.ca.umg.business.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.stereotype.Service;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;

@Service
public class DateValidator {

    private static final String VALID_FROM_FIELD = "validFrom";
    private static final String AND_SEPERATOR = " and ";

    public final void validateDates(final SyndicateDataContainerInfo bean, final List<SyndicateData> previousVersions,
            final List<ValidationError> errors) throws BusinessException {
        validateFromAndToDates(bean, errors);

        if (isActiveFromPriorToActiveFromPreviousVersions(bean.getValidFrom(), previousVersions)) {
            errors.add(new ValidationError(VALID_FROM_FIELD,
                    "'Active From' should not be prior to 'Active From' of previous version."));
            return;
        }
        Long activeToPreviousVersion = getActiveToPreviousVersion(previousVersions);
        checkForOverlapPeriod(bean, activeToPreviousVersion);
        checkForTimeGap(bean, activeToPreviousVersion);
    }

    public void validateFromAndToDates(final SyndicateDataContainerInfo bean, List<ValidationError> dateValidationErrors) {
        if (isActiveUntilPriorToActiveFrom(bean)) {
            dateValidationErrors.add(new ValidationError(VALID_FROM_FIELD,
                    "'Active Untill' should not be prior to 'Active From'."));
        }
        if (bean.getOldValidFrom() == null && isActiveFromPriorToCurrentDateTime(bean.getValidFrom())) {
            dateValidationErrors.add(new ValidationError(VALID_FROM_FIELD,
                    "'Active From' should not be prior to current date time."));
        } else if (bean.getOldValidFrom() != null && bean.getOldValidFrom().longValue() != bean.getValidFrom().longValue()
                && isActiveFromPriorToCurrentDateTime(bean.getValidFrom())) {
            dateValidationErrors.add(new ValidationError(VALID_FROM_FIELD,
                    "'Active From' should not be prior to current date time."));
        }
    }

    private void checkForTimeGap(final SyndicateDataContainerInfo bean, Long activeToPreviousVersion) throws BusinessException {
        if (isSpecified(activeToPreviousVersion)
                && isNotActiveFromLessThanActiveToPreviousVersion(bean.getValidFrom(), activeToPreviousVersion)
                && isGapExistsActiveToPreviousVersionAndActiveFromCurrent(activeToPreviousVersion, bean.getValidFrom())
                && isNotAgreedToAdjustGapPeriod(bean)) {
            long overlapPeriod = getGapActiveToPreviousVersionAndActiveFromCurrent(activeToPreviousVersion, bean.getValidFrom());
            String message = getFormattedMessage(overlapPeriod);
            throw new BusinessException(BusinessExceptionCodes.BSE000034, new String[] { message });
        }
    }

    private void checkForOverlapPeriod(final SyndicateDataContainerInfo bean, Long activeToPreviousVersion)
            throws BusinessException {
        if (isSpecified(activeToPreviousVersion)
                && isActiveFromLessThanActiveToPreviousVersion(bean.getValidFrom(), activeToPreviousVersion)
                && isNotAgreedToAdjustOverlapPeriod(bean)) {
            Interval interval = new Interval(bean.getValidFrom(), activeToPreviousVersion);
            long overlapPeriod = interval.toDuration().getStandardMinutes();
            String message = getFormattedMessage(overlapPeriod);
            throw new BusinessException(BusinessExceptionCodes.BSE000033, new String[] { message });
        }
    }

    private String getFormattedMessage(long overlapPeriod) {
        Period period = new Period(Minutes.minutes((int) overlapPeriod));
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendYears().appendSuffix(" year"," years").appendSeparator(AND_SEPERATOR).appendMonths().appendSuffix(" month", " months").appendSeparator(AND_SEPERATOR).appendWeeks().appendSuffix(" week", " weeks").appendSeparator(AND_SEPERATOR).appendDays().appendSuffix(" day", " days")
                .appendSeparator(AND_SEPERATOR).appendHours().appendSuffix(" hour", " hours").appendSeparator(AND_SEPERATOR).appendMinutes()
                .appendSuffix(" minute", " minutes").appendSeparator(AND_SEPERATOR).appendSeconds().appendSuffix(" second", " seconds")
                .toFormatter();
        return formatter.print(period.normalizedStandard());
    }

    private boolean isNotAgreedToAdjustGapPeriod(final SyndicateDataContainerInfo bean) {
        return bean.getAction() != UserConfirmation.AGREED_TO_ADJUST_TIME_GAP;
    }

    private boolean isNotAgreedToAdjustOverlapPeriod(final SyndicateDataContainerInfo bean) {
        return bean.getAction() != UserConfirmation.AGREED_TO_ADJUST_TIME_OVERLAP;
    }

    private boolean isNotActiveFromLessThanActiveToPreviousVersion(Long validFrom, Long activeToPreviousVersion) {
        return !isActiveFromLessThanActiveToPreviousVersion(validFrom, activeToPreviousVersion);
    }

    private long getGapActiveToPreviousVersionAndActiveFromCurrent(final Long activeToPreviousVersion,
            final Long activeFromCurrent) {
        Interval interval = new Interval(activeToPreviousVersion, activeFromCurrent);
        return interval.toDuration().getStandardMinutes();
    }

    private boolean isGapExistsActiveToPreviousVersionAndActiveFromCurrent(final Long activeToPreviousVersion,
            final Long activeFromCurrent) {
        return getGapActiveToPreviousVersionAndActiveFromCurrent(activeToPreviousVersion, activeFromCurrent) > 1;

    }

    private boolean isActiveFromLessThanActiveToPreviousVersion(final Long activeFrom, final Long activeToPreviousVersion) {
        return isPriorTo(activeFrom, activeToPreviousVersion);
    }

    private Long getActiveToPreviousVersion(final List<SyndicateData> previousVersions) {
        Long activeToPreviousVersion = null;
        if (!CollectionUtils.isEmpty(previousVersions)) {
            // TODO : this is will have to be changed to equals
            SyndicateData previousVersion = previousVersions.get(0);
            activeToPreviousVersion = previousVersion.getValidTo();
        }
        return activeToPreviousVersion;
    }

    private boolean isActiveFromPriorToCurrentDateTime(final Long activeFrom) {
        return isPriorTo(activeFrom, new DateTime().getMillis());
    }

    private boolean isActiveFromPriorToActiveFromPreviousVersions(final Long activeFrom,
            final List<SyndicateData> previousVersions) {
        boolean result = false;
        for (Long activeFromPreviousVersion : getActiveFromPreviousVersions(previousVersions)) {
            if (activeFromPreviousVersion != null && isPriorTo(activeFrom, activeFromPreviousVersion)) {
                result = true;
            }
        }
        return result;
    }

    private List<Long> getActiveFromPreviousVersions(final List<SyndicateData> previousVersions) {
        List<Long> activeFromPreviousVersions = new ArrayList<Long>();
        for (SyndicateData version : previousVersions) {
            activeFromPreviousVersions.add(version.getValidFrom());
        }
        return activeFromPreviousVersions;
    }

    private boolean isActiveUntilPriorToActiveFrom(final SyndicateDataContainerInfo bean) {
        return isSpecified(bean.getValidTo()) ? isPriorTo(bean.getValidTo(), bean.getValidFrom()) : false;
    }

    private boolean isSpecified(final Long validTo) {
        return validTo != null && validTo != 0;
    }

    private boolean isPriorTo(final Long dateTime, final Long dateTime2) {
        return dateTime.compareTo(dateTime2) < 0;
    }
}
