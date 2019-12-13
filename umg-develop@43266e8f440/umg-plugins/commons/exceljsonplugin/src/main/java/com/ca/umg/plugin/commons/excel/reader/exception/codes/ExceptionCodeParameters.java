/**
 * 
 */
package com.ca.umg.plugin.commons.excel.reader.exception.codes;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Named;

/**
 * @author raddibas
 *
 */
@Named
public class ExceptionCodeParameters implements Serializable {
	
	private static final long serialVersionUID = 618151937793694765L;
	
	private ArrayList<Object> exceptionCodeParams;

	public ArrayList<Object> getExceptionCodeParams() {
		return exceptionCodeParams;
	}

	public void setExceptionCodeParams(ArrayList<Object> exceptionCodeParams) {
		this.exceptionCodeParams = exceptionCodeParams;
	}
	
	public Object[] getExceptionCodeParamsAsArray() {
		if (exceptionCodeParams != null) {
			return exceptionCodeParams.toArray();
		} else {
			return null;
		}
	}

}
