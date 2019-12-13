package com.ca.umg.business.common.info;

/**
 * 
 * @author kabiju
 * For server side pagination, ajax call has to return paging information and business data 
 * This wrapper class is generic and can be used for multiple places
 * @param <T>
 */
public class ResponseWrapper <T> {
	private T response;
	private PagingInfo pagingInfo;
	
	public T getResponse(){
		return response;
	}
	public void setResponse(T response){
		this.response = response;
	}
	public PagingInfo getPagingInfo(){
		return pagingInfo;
	}
	public void setPagingInfo(PagingInfo pagingInfo){
		this.pagingInfo = pagingInfo;
	}

}
