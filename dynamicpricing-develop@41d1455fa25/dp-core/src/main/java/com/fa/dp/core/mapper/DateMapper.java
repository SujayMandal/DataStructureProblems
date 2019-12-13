package com.fa.dp.core.mapper;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapstruct.Mapper;

import static com.fa.dp.core.util.DateConversionUtil.EST_TIME_ZONE;

@Mapper(componentModel = "spring")
public class DateMapper {

   public DateTime toDateValue(Long value) {
       return new DateTime(value, DateTimeZone.forTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE)));
   }

    public Long toLongValue(DateTime dateTime) {
        return dateTime.toDateTime(DateTimeZone.UTC).getMillis();
    }

}
