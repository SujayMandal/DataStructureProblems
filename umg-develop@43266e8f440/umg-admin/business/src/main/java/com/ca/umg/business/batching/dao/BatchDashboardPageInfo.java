package com.ca.umg.business.batching.dao;

public class BatchDashboardPageInfo {
	private Long selectedPage;
	private Long totalPages;
	private Long totalRecords;
	private Integer pageSize;
	
	public Long getSelectedPage() {
		return selectedPage;
	}
	public void setSelectedPage(Long selectedPage) {
		this.selectedPage = selectedPage;
	}
	public Long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}
	public Long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
