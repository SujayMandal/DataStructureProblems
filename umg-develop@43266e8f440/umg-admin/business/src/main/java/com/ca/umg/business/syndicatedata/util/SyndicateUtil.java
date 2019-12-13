/**
 * 
 */
package com.ca.umg.business.syndicatedata.util;

import org.apache.commons.lang.StringUtils;

import com.ca.umg.business.constants.BusinessConstants;

/**
 * @author kamathan
 *
 */
public final class SyndicateUtil {

    private SyndicateUtil() {

    }

    public static String formatSyndicateColumnName(String name) {
        String formattedName = name;
        if (StringUtils.startsWith(name, BusinessConstants.SYND_CLMN_NAME_ESC_CHAR)) {
            formattedName = StringUtils.substring(formattedName, 1);
        }
        return formattedName;
    }
}
