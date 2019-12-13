package com.ca.umg.rt.flows.generator;

public class QueryMetaData implements MetaData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1715527354776248693L;

	private String id;
	
	private String inputChannelName;
	
	private String outputChannelName;
	
	private String requestChannelName;
	
	private String replyChannelName;
	
	private String queryResponseName;
	
	private String jdbcQueryId;
	
	private String sql;
	
	private boolean maxRowsPerPoll;
	
	private boolean rowMapperCondition;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInputChannelName() {
		return inputChannelName;
	}

	public void setInputChannelName(String inputChannelName) {
		this.inputChannelName = inputChannelName;
	}

	public String getOutputChannelName() {
		return outputChannelName;
	}

	public void setOutputChannelName(String outputChannelName) {
		this.outputChannelName = outputChannelName;
	}

	public String getRequestChannelName() {
		return requestChannelName;
	}

	public void setRequestChannelName(String requestChannelName) {
		this.requestChannelName = requestChannelName;
	}

	public String getReplyChannelName() {
		return replyChannelName;
	}

	public void setReplyChannelName(String replyChannelName) {
		this.replyChannelName = replyChannelName;
	}

	public String getQueryResponseName() {
		return queryResponseName;
	}

	public void setQueryResponseName(String queryResponseName) {
		this.queryResponseName = queryResponseName;
	}

	public String getJdbcQueryId() {
		return jdbcQueryId;
	}

	public void setJdbcQueryId(String jdbcQueryId) {
		this.jdbcQueryId = jdbcQueryId;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean isMaxRowsPerPoll() {
		return maxRowsPerPoll;
	}

	public void setMaxRowsPerPoll(boolean maxRowsPerPoll) {
		this.maxRowsPerPoll = maxRowsPerPoll;
	}

	public boolean isRowMapperCondition() {
		return rowMapperCondition;
	}

	public void setRowMapperCondition(boolean rowMapperCondition) {
		this.rowMapperCondition = rowMapperCondition;
	}
}
