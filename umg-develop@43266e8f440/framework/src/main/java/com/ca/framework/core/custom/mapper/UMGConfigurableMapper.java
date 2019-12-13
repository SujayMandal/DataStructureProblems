package com.ca.framework.core.custom.mapper;

import javax.inject.Named;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Named
public class UMGConfigurableMapper extends ConfigurableMapper {

    public UMGConfigurableMapper() {
        super();
    }

    @Override
    protected void configure(MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalDateTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalTime.class));
        
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(OrikaConverter.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(UMGPassThrough.class));
        for(BeanDefinition bd : scanner.findCandidateComponents("com.ca")) {
            try {
                Class<?> c = Class.forName(bd.getBeanClassName());
                if (bd.getBeanClassName().contains("DatatypeInfo")) {
                	factory.getConverterFactory().registerConverter(new PassThroughConverter(c));
                } else {
                    Converter<?, ?> converter = (Converter<?, ?>) c.newInstance();
                    factory.getConverterFactory().registerConverter(converter);
                }
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                System.err.println(e);
            }
        }
    }

    // public <S, T> Page<T> mapAsPage(Page<S> source, Class<T> target, Pageable pageable) {
    // List<T> content = mapAsList(source.getContent(), target);
    // return new PageImpl<T>(content, pageable, source.getTotalElements());
    // }

}