package com.ca.umg.modelet.config;

import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.regex.Pattern;

public class RegexFilter extends RegexPatternTypeFilter {

    /*public RegexFilter() {
        super(Pattern.compile(".*audit.*"));
    }*/

    public RegexFilter() {
        super(Pattern.compile(".*ca.umg.notification.*|.*ca.pool.*|.*audit.*"));
    }

}