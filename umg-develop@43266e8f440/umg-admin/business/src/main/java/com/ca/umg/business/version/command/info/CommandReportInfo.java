/**
 * 
 */
package com.ca.umg.business.version.command.info;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ca.umg.business.version.command.Command;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.report.model.ReportInfo;

/**
 * This class will hold all reporting data along with the error generated while command execution.
 * 
 * @author chandrsa
 *
 */
public class CommandReportInfo {

    private String versionId;
    private List<Error> errors;
    private boolean success;
    private Deque<Command> executedSteps;
    private boolean rollback;
    private VersionInfo versionInfo;
    private String transactionId; 
    private Map<String, Object> modelExceptions;
    private ReportInfo reportInfo;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
    
    public void addError(Error error){
        if(errors == null){
            errors = new LinkedList<Error>();
        }
        errors.add(error);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Deque<Command> getExecutedSteps() {
        return executedSteps;
    }

    public void setExecutedSteps(Deque<Command> executedSteps) {
        this.executedSteps = executedSteps;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

	public VersionInfo getVersionInfo() {
		return versionInfo;
	}

	public void setVersionInfo(VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

    public Map<String, Object> getModelExceptions() {
        return modelExceptions;
    }

    public void setModelExceptions(Map<String, Object> modelExceptions) {
        this.modelExceptions = modelExceptions;
    }
    
    public ReportInfo getReportInfo() {
    	return reportInfo;
    }
    
    public void setReportInfo(final ReportInfo reportInfo) {
    	this.reportInfo = reportInfo;
    }    
}
