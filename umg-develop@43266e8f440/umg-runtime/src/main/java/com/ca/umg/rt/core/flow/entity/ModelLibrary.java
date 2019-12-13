/*
 * ModelLibrary.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.entity;

import java.io.Serializable;

/**
 * 
 **/
public class ModelLibrary implements Serializable {
	private static final long serialVersionUID = 240669251755630939L;
	private String name;
    private String umgName;
    private String description;
    private String language;
    private String type;
    private String jarName;
    private String excEnv;
    private String checksum;

    /**
     * DOCUMENT ME!
     *
     * @return the name
     **/
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name
     *            the name to set
     **/
    public void setName(String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the umgName
     **/
    public String getUmgName() {
        return umgName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param umgName
     *            the umgName to set
     **/
    public void setUmgName(String umgName) {
        this.umgName = umgName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the description
     **/
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param description
     *            the description to set
     **/
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the type
     **/
    public String getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type
     *            the type to set
     **/
    public void setType(String type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the jarName
     **/
    public String getJarName() {
        return jarName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jarName
     *            the jarName to set
     **/
    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the language
     **/
    public String getLanguage() {
        return language;
    }

    /**
     * DOCUMENT ME!
     *
     * @param language
     *            the language to set
     **/
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExcEnv() {
        return excEnv;
    }

    public void setExcEnv(String excEnv) {
        this.excEnv = excEnv;
    }

	/**
	 * Checksum value
	 * @return
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}
