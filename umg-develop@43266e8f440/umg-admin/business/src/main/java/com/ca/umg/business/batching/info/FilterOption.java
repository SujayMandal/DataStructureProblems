package com.ca.umg.business.batching.info;

public class FilterOption {

    private String fromDate;
    private String toDate;
	private String batchId;
	private String fileName;

    private Long startTime;

    private Long endTime;

	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FilterOption [fromDate=" + fromDate + ", toDate=" + toDate + ", batchId=" + batchId + ", fileName=" + fileName
                + ", startTime=" + startTime + ", endTime=" + endTime + "]";
    }
	

}
