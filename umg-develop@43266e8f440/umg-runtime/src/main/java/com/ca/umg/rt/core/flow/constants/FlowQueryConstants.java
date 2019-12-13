/*
 * FlowQueryConstatnts.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.constants;

/**
 * 
 **/
public final class FlowQueryConstants
{
    
    public static final String GET_ALL_ACTIVE_TENANT = "SELECT T.NAME,T.DESCRIPTION,T.CODE,SK.KEY_TYPE, SK.SYSTEM_KEY,TC.CONFIG_VALUE,AT.AUTH_CODE \n"
            + "FROM TENANT T, TENANT_CONFIG TC, AUTHTOKEN AT,SYSTEM_KEY SK \n"
            + "WHERE T.ID=TC.TENANT_ID    AND SK.ID=TC.SYSTEM_KEY_ID AND AT.TENANT_ID=T.ID AND AT.STATUS='Active'";
    public static final String GET_ALL_PENDING_TENANT = "SELECT T.NAME,T.DESCRIPTION,T.CODE,SK.KEY_TYPE, SK.SYSTEM_KEY,TC.CONFIG_VALUE,AT.AUTH_CODE \n"
            + "FROM TENANT T, TENANT_CONFIG TC, AUTHTOKEN AT,SYSTEM_KEY SK \n"
            + "WHERE T.ID=TC.TENANT_ID    AND SK.ID=TC.SYSTEM_KEY_ID AND AT.TENANT_ID=T.ID AND (AT.STATUS NOT IN('Active') AND AT.STATUS='Pending')";
	public static final String GET_ALL_VERSION_MAPPING = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION,V.MINOR_VERSION \n" +
	                                                                    ",M.NAME AS MAPPING_NAME, M.MODEL_IO_DATA AS MODEL_IO_DATA \n" +
	                                                                    ",MI.MAPPING_DATA AS INPUT_MAPPING_DATA, MI.TENANT_INTERFACE_DEFINITION AS INPUT_TID \n" +
	                                                                    ",MO.MAPPING_DATA AS OUTPUT_MAPPING_DATA, MO.TENANT_INTERFACE_DEFINITION AS OUTPUT_TID, ML.ALLOW_NULL AS ALLOW_NULL FROM UMG_VERSION V \n" +
	                                                                    "JOIN MAPPING M ON V.MAPPING_ID = M.ID \n" +
	                                                                    "JOIN MAPPING_INPUT MI ON M.ID = MI.MAPPING_ID \n" +
	                                                                    "LEFT JOIN MAPPING_OUTPUT MO ON M.ID = MO.MAPPING_ID JOIN MODEL ML ON ML.ID = M.MODEL_ID \n" +
	                                                                    "WHERE V.TENANT_ID=:TENANT_ID AND V.STATUS=\"PUBLISHED\"";
	public static final String GET_ALL_VERSION_QUERY   = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION,V.MINOR_VERSION \n" +
	                                                                    ",M.NAME AS MAPPING_NAME \n" +
	                                                                    ",Q.NAME AS QUERY_NAME,Q.EXEC_SEQUENCE,Q.EXEC_QUERY AS QUERY_STRING,Q.ROW_TYPE,Q.DATA_TYPE FROM UMG_VERSION V \n" +
	                                                                    "JOIN MAPPING M ON V.MAPPING_ID = M.ID \n" +
	                                                                    "JOIN SYNDICATE_DATA_QUERY Q ON M.ID = Q.MAPPING_ID \n" +
	                                                                    "WHERE V.TENANT_ID=:TENANT_ID AND V.STATUS=\"PUBLISHED\" \n" +
	                                                                    "ORDER BY Q.EXEC_SEQUENCE";
	public static final String GET_ALL_VERSION_LIBRARY    = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION,V.MINOR_VERSION \n" +
	                                                                    ",ML.NAME AS MODEL_LIBRARY_NAME,ML.UMG_NAME MODEL_LIBRARY_UMG_NAME \n" +
	                                                                    ",ML.DESCRIPTION AS MODEL_LIBRARY_DESCRIPTION \n" +
	                                                                    ",ML.EXECUTION_LANGUAGE,ML.EXECUTION_TYPE,ML.JAR_NAME,ML.EXECUTION_ENVIRONMENT, ML.CHECKSUM_VALUE AS CHECKSUM FROM UMG_VERSION V \n" +
	                                                                    "JOIN MODEL_LIBRARY ML ON V.MODEL_LIBRARY_ID = ML.ID \n" +
	                                                                    "WHERE V.TENANT_ID=:TENANT_ID AND V.STATUS=\"PUBLISHED\"";
	
	public static final String GET_ALL_VERSION                        = "SELECT V.ID AS VERSION_ID,V.NAME AS VERSION_NAME, V.MAJOR_VERSION,V.MINOR_VERSION, V.MODEL_TYPE FROM UMG_VERSION V \n" +
	                                                                    "WHERE V.TENANT_ID=:TENANT_ID AND V.STATUS=\"PUBLISHED\"";
	
	public static final String GET_ALL_BATCH_ENABLED_TENANTS = "SELECT T.NAME,T.DESCRIPTION,T.CODE,SK.KEY_TYPE,SK.SYSTEM_KEY,TC.CONFIG_VALUE \n" +
	                                                                "FROM TENANT T, TENANT_CONFIG TC, SYSTEM_KEY SK \n"+ 
	                                                                "WHERE T.ID=TC.TENANT_ID AND SK.ID=TC.SYSTEM_KEY_ID \n"+
	                                                                "AND T.CODE IN (SELECT T.CODE FROM TENANT T, TENANT_CONFIG TC, SYSTEM_KEY SK \n"+ 
	                                                                "WHERE T.ID=TC.TENANT_ID AND TC.SYSTEM_KEY_ID = (SELECT ID FROM SYSTEM_KEY \n"+
	                                                                "WHERE SYSTEM_KEY = 'BATCH_ENABLED') AND UPPER(TC.CONFIG_VALUE) = 'TRUE' GROUP BY T.CODE)";
	
	public static final String GET_BATCH_ENABLED_CONFIGS = "SELECT SK.SYSTEM_KEY, TC.CONFIG_VALUE " +
			                                                        "FROM TENANT_CONFIG TC, SYSTEM_KEY SK, TENANT T " +
			                                                        "WHERE T.CODE = :TENANT_CODE AND TC.TENANT_ID = T.ID " +
			                                                        "AND TC.SYSTEM_KEY_ID IN (SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE = :WRAPPER_TYPE) " +
			                                                        "AND SK.ID = TC.SYSTEM_KEY_ID";
	public static final String GET_ALL_ENABLED_WRAPPERS = "SELECT SK.SYSTEM_KEY FROM SYSTEM_KEY SK, TENANT T, TENANT_CONFIG TC " + 
			                                                        "WHERE SK.KEY_TYPE = 'TENANT_WRAPPER' AND TC.SYSTEM_KEY_ID = SK.ID " +
			                                                        "AND UPPER(TC.CONFIG_VALUE) = 'TRUE' AND T.CODE = :TENANT_CODE " +
			                                                        "AND TC.TENANT_ID = T.ID";

    public static final String GET_AUTHTOKEN = "SELECT AT.AUTH_CODE FROM TENANT T, AUTHTOKEN AT WHERE T.ID=AT.TENANT_ID AND AT.STATUS='Pending' AND T.CODE=?";

	private FlowQueryConstants(){}

}
