/*
 * ModeletExceptionCodes.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.exception;

/**
 * Modelet exception codes MOBE indicates Business Exceptions, MOSE indicates System Exceptions
 **/
public final class ModeletExceptionCodes {
    // MOdelet Business Exceptions
    public static final String MOBE000001 = "MOBE000001".intern();
    public static final String MOBE000002 = "MOBE000002".intern();
    public static final String MOBE000003 = "MOBE000003".intern();
    public static final String MOBE000004 = "MOBE000004".intern();
    public static final String MOBE000005 = "MOBE000005".intern();

    // MOdelet System Exceptions
    /**
     * Error code for R model execution failure
     */
    public static final String MOSE000001 = "MOSE000001".intern();

    /**
     * Error code for failure to load R support packages
     */
    public static final String MOSE000002 = "MOSE000002".intern();

    /**
     * Error code for failure to load R package
     */
    public static final String MOSE000003 = "MOSE000003".intern();

    /**
     * Error code for failure to clear library path for R model
     */
    public static final String MOSE000004 = "MOSE000004".intern();

    /**
     * Error code for failure to create installation folder for R package
     */
    public static final String MOSE000005 = "MOSE000005".intern();

    /**
     * Error code for failure to unload model
     */
    public static final String MOSE000006 = "MOSE000006".intern();

    /**
     * Error code for failure to unload R packages
     */
    public static final String MOSE000007 = "MOSE000007".intern();

    /**
     * Error code for failure to load packages dynamically
     */
    public static final String MOSE000008 = "MOSE000008".intern();

    /**
     * Error code for failure of model execution engine startup
     */
    public static final String MOSE000100 = "MOSE000100".intern();

    public static final String MOSE000101 = "MOSE000101".intern();

    private ModeletExceptionCodes() {
    }
}