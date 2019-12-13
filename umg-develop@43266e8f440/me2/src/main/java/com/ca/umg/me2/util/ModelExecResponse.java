/**
 * 
 */
package com.ca.umg.me2.util;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class ModelExecResponse<T> implements Serializable {

    private static final long serialVersionUID = -6299303179279350599L;

    private T response;

    private boolean success = true;

    private String message;

    private String errorCode;
    
    /**
     * Execution Time taken at M2E component (M2E Waiting time + Modelet Time + network time) 
     */
    private Long me2ExecutionTime;

    private String host;
    private Integer port;
    private String memberHost;
    private Integer memberPort;
    private String poolName;
    private String poolCriteria;
    private String serverType;
    private String contextPath;
    private int rServePort;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public void setMe2ExecutionTime(final Long me2ExecutionTime) {
    	this.me2ExecutionTime = me2ExecutionTime;
    }

    public Long getMe2ExecutionTime() {
    	return me2ExecutionTime;
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getMemberHost() {
		return memberHost;
	}

	public void setMemberHost(String memberHost) {
		this.memberHost = memberHost;
	}

	public Integer getMemberPort() {
		return memberPort;
	}

	public void setMemberPort(Integer memberPort) {
		this.memberPort = memberPort;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getPoolCriteria() {
		return poolCriteria;
	}

	public void setPoolCriteria(String poolCriteria) {
		this.poolCriteria = poolCriteria;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public int getrServePort() {
		return rServePort;
	}

	public void setrServePort(int rServePort) {
		this.rServePort = rServePort;
	}
	
}