/**
 * 
 */
package com.fa.dp.core.mapper;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

/**
 *
 *
 */
@Named
public class BeanMapper extends ConfigurableMapper {

    public BeanMapper() {
        super();
    }

    @Override
    protected void configure(MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalDateTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalDate.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(java.time.LocalDate.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(java.time.LocalDateTime.class));
    }

}
