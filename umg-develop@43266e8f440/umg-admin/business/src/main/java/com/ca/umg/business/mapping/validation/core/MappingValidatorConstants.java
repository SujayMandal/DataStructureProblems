package com.ca.umg.business.mapping.validation.core;

/**
 * 
 * @author mahantat
 * 
 */
public final class MappingValidatorConstants {

    public static final String INPUT_MAPPING_VALIDATOR = "InputMappingValidator";
    public static final String DATA_TYPE_VALIDATOR = "DataTypeValidator";
    public static final String MANDATORY_MAPPING_VALIDATOR = "MandatoryMappingValidator";
    public static final String OPTIONAL_MAPPING_VALIDATOR = "OptionalMappingValidator";
    public static final String OPT_MID_MAPPING_VALIDATOR = "OptionalMidMappingValidator";
    public static final String OPT_SYND_MID_MAPPING_VALIDATOR = "OptionalSyndicateMidMappingValidator";
    public static final String MAND_MID_MAPPING_VALIDATOR = "MandatoryMidMappingValidator";
    public static final String MAND_SYND_MID_MAPP_VALIDATOR = "MandatorySyndicateMidMappingValidator";
    public static final String NULL_VALIDATOR = "NullValidator";

    public static final String TID_SQL_MANDATORY_ERROR_MSG = "Tid input parameter for sql should be mandatory";
    public static final String TID_OPTIONAL_ERROR_MSG = "Tid parameter should be optional";
    public static final String SYN_MAND_MID_VALIDATOR = "SyndMandMidValidator";
    public static final String TID_MANDATORY_ERROR_MSG = "One mandatory Tid Parameter should be mapped to a mandatory Mid Parameter.";
    public static final String TID_ONLY_ONE_MAND_ERROR_MSG = "Only One mandatory Tid Parameter should be mapped to a mandatory Mid Parameter.";
    public static final String DATATYPE_ERROR_MSG = "Datatype of all mapped elements are not same.";
    public static final String MAPPING_PARAM_NOT_FOUND = "Mapping params not found. Internal Error.";
    public static final String MORE_THAN_TWO_MAPP_ERROR = "Contains more than 2 mapping elements";
    public static final String LESS_THAN_ONE_MAPP_ERROR = "At least One Tid parameter should be mapped.";
    public static final String MORE_THAN_ONEMAPP_ERROR = "At most one Tid parameter should be Mapped.";

    public static final String TID_MAPPING_NOT_FOUND = "Tid Mapping element name is Null or Empty";
    public static final String TID_MAPPING_NOT_FOUND_IN_MAP = "Tid Mapping element is not available in the map";
    public static final String TID_MAPPING_DATATYPE_NOT_FOUND = "Tid Element Datatype is null";

    public static final String MID_MAPPING_NOT_FOUND = "Mid Mapping element name is Null or Empty";
    public static final String MID_MAPPING_NOT_FOUND_IN_MAP = "Mid Mapping element is not available in the map";
    public static final String MID_MAPPING_DATATYPE_NOT_FOUND = "Mid Element Datatype is null";

    private MappingValidatorConstants() {
        // Empty Constructor
    }
}
