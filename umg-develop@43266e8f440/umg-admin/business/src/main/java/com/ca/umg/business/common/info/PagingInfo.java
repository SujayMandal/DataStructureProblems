/**
 * 
 */
package com.ca.umg.business.common.info;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.constants.BusinessConstants;

/**
 * @author kamathan
 *
 */
public class PagingInfo extends BaseInfo {
    
    private static final long serialVersionUID = 8115855512829894851L;

    private int page;
    
    // used in tenant portal, pagination
    private int pageSet;




	private int pageSize;

    private String sortColumn;

    private boolean descending;

    private String searchString;
    
    private Long totalElements;
    
    private Integer totalPages;

    public Long getTotalElements() {
		return totalElements;
	}

    public int getPageSet(){
		return pageSet;
	}

	public void setPageSet(int pageSet){
		this.pageSet = pageSet;
	}
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }
    
    /**
     * This static method can be used when server side pagination implements
     * this one is written as static because it can be used in general
     * @param pageInfo
     * @return
     */

    public static Pageable constructPageSpecification(PagingInfo pageInfo) {
    	return  new PageRequest(pageInfo.getPage() == BusinessConstants.NUMBER_ZERO ? 
    			BusinessConstants.NUMBER_ZERO : pageInfo.getPage() - 1, pageInfo.getPageSize(), PagingInfo.sortOption(pageInfo));
    }
    
    // this sorting will ignore case 
    public static Sort sortOption(PagingInfo pageInfo) {
    	Sort sort=null;
    	Sort.Order order = new Sort.Order(pageInfo.isDescending()?Sort.Direction.DESC:Sort.Direction.ASC, pageInfo.getSortColumn()).ignoreCase();
    	
    	if(pageInfo.getSortColumn()==null||pageInfo.getSortColumn().isEmpty()){
    		sort= new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_DATE);
    	}else{
    		sort=new Sort(order);
    	}
        return sort;
    }
    
    
    /**
    This method will manually set the paging information base on
    number of record in the list, and existing pagingInfo
    */
    public static PagingInfo  setPagingForList(List list, PagingInfo  currentPageInfo) {
	   PagingInfo newPageInfo=new PagingInfo();
	   
	   if(CollectionUtils.isNotEmpty(list) && currentPageInfo!=null){
		   newPageInfo.setPageSize(currentPageInfo.getPageSize());
		   double totalRecord=list.size();
		   Double totalPages=Math.ceil(totalRecord/currentPageInfo.getPageSize());
		   newPageInfo.setTotalPages(totalPages.intValue());
		   newPageInfo.setPage(currentPageInfo.getPage()>totalPages.intValue()?1:currentPageInfo.getPage());
	   }
   	   return newPageInfo;
   } 
    
    
    /**
     * This method will trim the list based on the pagingInfo
     * @param dataList
     * @param pageInfo
     * @return
     */
    public static List getPagedList(List dataList, PagingInfo  pageInfo){
 		List subList = null;
 		 if(CollectionUtils.isNotEmpty(dataList)){
 			int recordSize = dataList.size();
 			int fromIndex = pageInfo.getPage() * pageInfo.getPageSize()-pageInfo.getPageSize();
 			int toIndex=(fromIndex+pageInfo.getPageSize())>=recordSize?recordSize:fromIndex+pageInfo.getPageSize();
 		
 			if(recordSize >= fromIndex){
 				subList = dataList.subList(fromIndex, toIndex);
 			}
 		}
 		return subList;
 	}
    
    
    
    

}
