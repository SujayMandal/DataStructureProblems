package com.ca.umg.business.dashboard.bo;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dashboard.dao.DashBoardDAO;
import com.ca.umg.business.dashboard.info.ModelUsageInfo;
import com.ca.umg.business.dashboard.info.ModelUsagePattern;
import com.ca.umg.business.dashboard.info.ModelVersionStatus;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.dao.VersionDAO;
import com.ca.umg.business.version.info.VersionStatus;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;

@Named
public class DashBoardBOImpl implements DashBoardBO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DashBoardBOImpl.class);
	
	@Inject
	private DashBoardDAO dashBoardDAO;
	
	@Inject
	private VersionDAO versionDAO;
	
	private static final String UNIQUE_KEY = "UNIQUE";
	
	private static final String LIMIT = "100";
	

	@Override
	public List<ModelVersionStatus> getVersionStats() throws BusinessException, SystemException {
		LOGGER.info("Getting the version statistics DashBoardBOImpl::getVersionStats");
		List<ModelVersionStatus> statsList = null;
		ModelVersionStatus modelVersionStatus = null;
		try {
			statsList = new ArrayList<>();
			Long uniqueVersionCount = dashBoardDAO.countUniqueVersions();
			modelVersionStatus = new ModelVersionStatus();
			modelVersionStatus.setCount(uniqueVersionCount);
			modelVersionStatus.setVersionStatus(UNIQUE_KEY);
			statsList.add(modelVersionStatus);
			
			Long versionStatusCount = dashBoardDAO.countVersionsUsingStatus(VersionStatus.PUBLISHED.getVersionStatus());
			modelVersionStatus = new ModelVersionStatus();
			modelVersionStatus.setCount(versionStatusCount);
			modelVersionStatus.setVersionStatus(VersionStatus.PUBLISHED.getVersionStatus());
			statsList.add(modelVersionStatus);
		} catch (DataAccessException ex) {
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getVersionStats",ex);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for verssionStatistics DashBoardBOImpl::getVersionStats", ex });
        }
		
		return statsList;
	}

	@Override
	public Long getTransactionsCountForDay(Integer day) throws BusinessException,
			SystemException {
		LOGGER.info("Getting the version statistics DashBoardBOImpl::getTransactionsCountForDay for {} days: ",day);
		Long currentTime = null;
		Long daysInMillis = null;  
		Long count = null;
		try {
			currentTime = System.currentTimeMillis();
			daysInMillis = TimeUnit.DAYS.toMillis(day != null ? day.longValue() : 1 ); 
			count = dashBoardDAO.countVersionsByDay(currentTime-daysInMillis);
		} catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getTransactionsCountForDay",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for version data count for day DashBoardBOImpl::getTransactionsCountForDay", ex });
        }
		return count;
	}
	
	@Override
	public Map<String,Long> getScsFailCntForTransactions (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the version statistics DashBoardBOImpl::getScsFailCntForTransactions");
		Long runAsOfDateFrom = null; 
		Long runAsOfDateTo = null;
		Map<String,Long> transactionSucFailCnt = null;
		try {
			transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
	        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
	        checkValidFromToDates(transactionFilter);

	        runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
	        runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
	        if(transactionFilter.getTenantNames().length == BusinessConstants.NUMBER_ZERO) {
	        	transactionFilter.setTenantNames(new String[] {RequestContext.TENANT_CODE});
	        }
	        transactionSucFailCnt = dashBoardDAO.getScsFailCntForTransactions(runAsOfDateFrom, runAsOfDateTo,transactionFilter.getTenantNames());
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getScsFailCntForTransactions",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for version data count for day DashBoardBOImpl::getScsFailCntForTransactions", ex });
        }
		return transactionSucFailCnt;
	}
	
	@Override
	public Map<String,Object> getstatusMetricsForTransactions (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the status Metrics DashBoardBOImpl::getstatusMetricsForTransactions");
		Map<String,Object> transactionSucFailCnt = null;
		Long runAsOfDateFrom = null; 
		Long runAsOfDateTo = null;
		try {
			transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
	        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
	        checkValidFromToDates(transactionFilter);

	        runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
	        runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
	        if(transactionFilter.getTenantNames().length == BusinessConstants.NUMBER_ZERO) {
	        	transactionFilter.setTenantNames(new String[] {RequestContext.TENANT_CODE});
	        }
	        transactionSucFailCnt = dashBoardDAO.getStatusMetricsTransactions(runAsOfDateFrom, runAsOfDateTo,transactionFilter.getTenantNames());
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getstatusMetricsForTransactions",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for status Metrics DashBoardBOImpl::getstatusMetricsForTransactions", ex });
        }
		return transactionSucFailCnt;
	}
	
	@Override
	public Map<String,Object> getUsageDynamicsForTransactions (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the usage Dynamics DashBoardBOImpl::getUsageDynamicsForTransactions");
		Map<String,Object> transactionSucFailCnt = null;
		Long runAsOfDateFrom = null; 
		Long runAsOfDateTo = null;
		try {
			transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
	        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
	        checkValidFromToDates(transactionFilter);
	        
	        Map<String,Object> groupByAndInItData = getGroupByColumn(transactionFilter);


	        runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
	        runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
	        
	        LOGGER.info("runAsOfDateFrom  {} To runAsOfDateTo {} ",runAsOfDateFrom,runAsOfDateTo);
	        
	        if(transactionFilter.getTenantNames().length == BusinessConstants.NUMBER_ZERO) {
	        	transactionFilter.setTenantNames(new String[] {RequestContext.TENANT_CODE});
	        }
	        if(StringUtils.equalsIgnoreCase(BusinessConstants.SELECTED, transactionFilter.getSelectionType())){
		        transactionSucFailCnt = dashBoardDAO.getSelectedUsageDynamicsDetails(transactionFilter,groupByAndInItData);
	        }else if(StringUtils.equalsIgnoreCase(BusinessConstants.ALL, transactionFilter.getSelectionType())){
		        transactionSucFailCnt = dashBoardDAO.getAllTntUsageDynamicsDetails(transactionFilter,groupByAndInItData);
	        }else {
		        transactionSucFailCnt = dashBoardDAO.getInitUsageDynamicsDetails(transactionFilter,groupByAndInItData);
	        }
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getUsageDynamicsForTransactions",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for Usage Dynamics DashBoardBOImpl::getstatusMetricsForTransactions", ex });
        }
		return transactionSucFailCnt;
	}
	
	@Override
	public List<Object> getUsageDynamicsGrid(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the Usage Dynamic Grid DashBoardBOImpl::getUsageDynamicsGrid");
		List<Object> topHundrdFailTxn = null;
		/*Long runAsOfDateFrom = null; 
		Long runAsOfDateTo = null;*/
		try {
			transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
	        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
	        checkValidFromToDates(transactionFilter);

	     /*   Long runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
	        Long runAsOfDateTo = transactionFilter.getRunAsOfDateTo();*/
	        if(transactionFilter.getSelectedTnt().length == BusinessConstants.NUMBER_ZERO) {
	        	transactionFilter.setSelectedTnt(new String[] {RequestContext.TENANT_CODE});
	        }
	        topHundrdFailTxn = dashBoardDAO.getUsageDynamicsGrid(transactionFilter);
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getUsageDynamicsGrid",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for Usage Top 100 Fail DashBoardBOImpl::getstatusMetricsForTransactions", ex });
        }
		return topHundrdFailTxn;
	}
	
	@Override
	public List<ModelVersionStatus> getFailTxn(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the Fail Txn DashBoardBOImpl::getTopHundredFailTxn");
		List<ModelVersionStatus> topHundrdFailTxn = null;
		Long runAsOfDateFrom = null; 
		Long runAsOfDateTo = null;
		try {
			transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
	        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
	        checkValidFromToDates(transactionFilter);

	        runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
	        runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
	        if(transactionFilter.getSelectedTnt().length == BusinessConstants.NUMBER_ZERO) {
	        	transactionFilter.setSelectedTnt(new String[] {RequestContext.TENANT_CODE});
	        }
	        topHundrdFailTxn = dashBoardDAO.getFailTxn(runAsOfDateFrom, runAsOfDateTo,transactionFilter.getSelectedTnt(),LIMIT);
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getTopHundredFailTxn",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for Usage Top 100 Fail DashBoardBOImpl::getstatusMetricsForTransactions", ex });
        }
		return topHundrdFailTxn;
	}
	
	@Override
	public Map<String, Object> getRaUsageTrendData(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the Fail Txn DashBoardBOImpl::getRaUsageTrendData");
		Map<String,Object> topHundrdFailTxn = null;
		Long runAsOfDateFrom = null; 
		Long runAsOfDateTo = null;
		try {

			transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
	        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
	        checkValidFromToDates(transactionFilter);

	        getGroupByColumn(transactionFilter);

	        runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
	        runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
	        LOGGER.info("Date From "+runAsOfDateFrom+" Date To "+runAsOfDateTo);
	        if(transactionFilter.getTenantNames().length == BusinessConstants.NUMBER_ZERO) {
	        	transactionFilter.setTenantNames(new String[] {RequestContext.TENANT_CODE});
	        }
	        topHundrdFailTxn = dashBoardDAO.getRaUsageTrendData(runAsOfDateFrom, runAsOfDateTo,transactionFilter.getTenantNames(),"");
	       
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getRaUsageTrendData",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for Usage Trend DashBoardBOImpl::getstatusMetricsForTransactions", ex });
        }
		return topHundrdFailTxn;
	}
	
	private Map<String,Object> getGroupByColumn(TransactionFilter transactionFilter) throws BusinessException {
		
		String groupBy = "";
		Map<String,Object> groupByAndInItData = new HashMap<>();
    	Map<String,Object> raTrendDataInIt = new HashMap<>();
    	Map<String,Long> modelTrendDataInIt = new HashMap<>();

		try {
		SimpleDateFormat format = new SimpleDateFormat(BusinessConstants.UMG_UTC_DATE_FORMAT,Locale.getDefault());
		DateTime start = new DateTime(format.parse(transactionFilter.getRunAsOfDateFromString()));
		DateTime end = new DateTime(format.parse(transactionFilter.getRunAsOfDateToString()));
/*		int months = Months.monthsBetween(start, end).getMonths();
*/		int days = Days.daysBetween(start, end).getDays();
/*		int hours = Hours.hoursBetween(start, end).getHours();
*/
		
		LOGGER.info("Days {}",days);
		/*//group By Year
        if(months>BusinessConstants.NUMBER_TWELVE){
        	groupBy = BusinessConstants.YEAR;
            transactionFilter.setExecutionGroup(BusinessConstants.YEAR_FORMAT);
            transactionFilter.setTotalElements(12l);
        }*/
		
		//group by hours
		if(days<BusinessConstants.NUMBER_FIVE) {
        	groupBy = BusinessConstants.HOUR;
            transactionFilter.setExecutionGroup(BusinessConstants.HOUR_FORMAT);
    		for (DateTime date = start; date.isBefore(end)||date.isEqual(end); date = date.plusHours(1))
    		{
            	Map<String, Long> raData = new HashMap<String, Long>();
				raData.put(BusinessConstants.SUCCESS, BusinessConstants.NUMBER_ZERO_LONG);
				raData.put(BusinessConstants.FAILURE_COUNT,BusinessConstants.NUMBER_ZERO_LONG);
				String dateKey = date.getYear()+BusinessConstants.HYPHEN+date.getMonthOfYear()+BusinessConstants.HYPHEN+date.getDayOfMonth()+BusinessConstants.HYPHEN+date.getHourOfDay();
            	raTrendDataInIt.put(dateKey,raData);
            	modelTrendDataInIt.put(dateKey, BusinessConstants.NUMBER_ZERO_LONG);
    		}
		}
		//Group By Days
        else if(days<BusinessConstants.NUMBER_NINETY) {
        	groupBy = BusinessConstants.DAY;
            transactionFilter.setExecutionGroup(BusinessConstants.DAY_YEAR_FORMAT);
            for (DateTime date = start; date.isBefore(end); date = date.plusDays(1))
    		{
            	Map<String, Long> raData = new HashMap<String, Long>();
				raData.put(BusinessConstants.SUCCESS, BusinessConstants.NUMBER_ZERO_LONG);
				raData.put(BusinessConstants.FAILURE_COUNT,BusinessConstants.NUMBER_ZERO_LONG);
				String dateKey = date.getYear()+BusinessConstants.HYPHEN+date.getMonthOfYear()+BusinessConstants.HYPHEN+date.getDayOfMonth();
            	raTrendDataInIt.put(dateKey,raData);
            	modelTrendDataInIt.put(dateKey, BusinessConstants.NUMBER_ZERO_LONG);
    		}
        }
        //Group By Month
        else {
        	groupBy =BusinessConstants.MONTH;
            transactionFilter.setExecutionGroup(BusinessConstants.MONTH_FORMAT);
            for (DateTime date = start; date.isBefore(end)||(date.getMonthOfYear() <= end.getMonthOfYear() && date.getYear() <= end.getYear()); date = date.plusMonths(1))//NOPMD
    		{
            	Map<String, Long> raData = new HashMap<String, Long>();
				raData.put(BusinessConstants.SUCCESS, BusinessConstants.NUMBER_ZERO_LONG);
				raData.put(BusinessConstants.FAILURE_COUNT,BusinessConstants.NUMBER_ZERO_LONG);
				String dateKey = date.getYear()+BusinessConstants.HYPHEN+date.getMonthOfYear()+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
				raTrendDataInIt.put(dateKey,raData);
            	modelTrendDataInIt.put(dateKey, BusinessConstants.NUMBER_ZERO_LONG);
    		}
          }
		} catch (ParseException e) {
			  LOGGER.error("BSE000076 : The Transaction Run Dates Range is invalid");
	            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000076, new Object[] {});
		}
		/*Long val = Long.valueOf((raTrendDataInIt.size()/10)+BusinessConstants.DOT+(raTrendDataInIt.size()/10));
		boolean flag = val>0 && raTrendDataInIt.size()>10;
		long ticksThreshhold = val;
		for(String ticks:raTrendDataInIt.keySet()) {
			if(flag || ticksThreshhold == val){
				ticksOnHaxis.add(ticks);
			}
			else {
				ticksThreshhold=0;			
				}
		}*/
		Long val =Long.valueOf(raTrendDataInIt.size()>BusinessConstants.NUMBER_TEN?BusinessConstants.NUMBER_TEN:raTrendDataInIt.size());
        transactionFilter.setTotalElements(val);       
		groupByAndInItData.put(BusinessConstants.GROUP_ID, groupBy);
		groupByAndInItData.put(BusinessConstants.RA_USAGE_TRENDLINE, raTrendDataInIt);
		groupByAndInItData.put(BusinessConstants.MODEL_USAGE_TRENDLINE, modelTrendDataInIt);


		return groupByAndInItData;
	}

	/*private String getGroupByColumn(TransactionFilter transactionFilter) {
		String groupBy = BusinessConstants.HOUR_DATE_FORMAT;
        transactionFilter.setExecutionGroup(BusinessConstants.HOUR_FORMAT);
        transactionFilter.setTotalElements(12l);
		String[] runAsOfDateFrom = transactionFilter.getRunAsOfDateFromString().split("-");
        String[] runAsOfDateTo = transactionFilter.getRunAsOfDateToString().split("-");
        if(!StringUtils.equalsIgnoreCase(runAsOfDateFrom[0], runAsOfDateTo[0])) {
        	groupBy = BusinessConstants.YEAR_DATE_FORMAT;
            transactionFilter.setExecutionGroup(BusinessConstants.YEAR_FORMAT);
            transactionFilter.setTotalElements(12l);
        }else if(!StringUtils.equalsIgnoreCase(runAsOfDateFrom[1], runAsOfDateTo[1])) {
        	groupBy = BusinessConstants.MONTH_DATE_FORMAT;
            transactionFilter.setExecutionGroup(BusinessConstants.MONTH_FORMAT);
            transactionFilter.setTotalElements(12l);
        }
        else if(!StringUtils.equalsIgnoreCase(runAsOfDateFrom[2].substring(0,2), runAsOfDateTo[2].substring(0,2))) {
        	groupBy = BusinessConstants.DAYS_DATE_FORMAT;
            transactionFilter.setExecutionGroup(BusinessConstants.DAY_FORMAT);
            transactionFilter.setTotalElements(12l);
        }
		return groupBy;
	}*/

	private void checkValidFromToDates(TransactionFilter transactionFilter) throws BusinessException {
        if (transactionFilter.getRunAsOfDateTo() != null && transactionFilter.getRunAsOfDateFrom() != null
                && transactionFilter.getRunAsOfDateFrom() > transactionFilter.getRunAsOfDateTo()) {
            LOGGER.error("BSE000076 : The Transaction Run Dates Range is invalid");
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000076, new Object[] {});
        }
    }
	
	@Override
	public List<ModelUsagePattern> getTransactionsCnt() throws BusinessException, SystemException {
		List<ModelUsagePattern> modelUsagePatterns = null;
		Map<String, ModelUsagePattern> modelUsagePatternMap = null;
		try {
			modelUsagePatternMap = new HashMap<>();
			Cursor cursor = dashBoardDAO.getTransactionsCnt();
			
			if (cursor != null) {
				
				while (cursor.hasNext()){
       				DBObject obj = cursor.next();
       				BasicDBObject basicDBObject = (BasicDBObject) obj.get(BusinessConstants.GROUP_ID);
       				ModelUsageInfo modelUsageInfo = new ModelUsageInfo();
       				modelUsageInfo.setTransactionCount(((Number) obj.get(BusinessConstants.TRANSACTION_COUNT)).longValue());
       				modelUsageInfo.setModelName(basicDBObject.getString(BusinessConstants.VERSION_NAME));
       				modelUsageInfo.setInterval(new DateFormatSymbols().getMonths()[Integer.parseInt(basicDBObject.getString(BusinessConstants.INTERVAL))-1]);
					String interval = modelUsageInfo.getInterval();
					if (modelUsagePatternMap.get(interval) == null) {
						ModelUsagePattern usagePattern = new ModelUsagePattern();
						List<ModelUsageInfo> usageInfos = new ArrayList<>();
						usageInfos.add(modelUsageInfo);
						usagePattern.setInterval(interval);
						usagePattern.setModelUsageInfos(usageInfos);
						modelUsagePatternMap.put(interval, usagePattern);
					} else {
						ModelUsagePattern usagePattern = modelUsagePatternMap.get(interval);
						List<ModelUsageInfo> usageInfos = usagePattern.getModelUsageInfos();
						usageInfos.add(modelUsageInfo);
						modelUsagePatternMap.put(interval, usagePattern);
					}
				}
				
				modelUsagePatterns = new ArrayList<>();
				for (String key: modelUsagePatternMap.keySet()) {
					ModelUsagePattern usagePattern = modelUsagePatternMap.get(key);
					modelUsagePatterns.add(usagePattern);
				}
			}					
		}catch (Exception ex) {// NOPMD
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getTransactionsCnt",ex);// NOPMD
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get version count for 90 day DashBoardBOImpl::getTransactionsCnt", ex });
        }
		
		return modelUsagePatterns;
	}

	@Override
	public List<String> getUniqueModelNames() throws BusinessException,
			SystemException {
		return versionDAO.getAllTenantModelNames();
	}
	
	@Override
	public Long getActiveLookupData() throws BusinessException,
			SystemException {
		LOGGER.info("Getting the Active LookUp Data statistics DashBoardBOImpl::getActiveLookupData ");
		Long currentTime = null;  
		Long count = null;
		try {
			currentTime = System.currentTimeMillis();
			count = dashBoardDAO.countActiveLookUpData(currentTime);
		} catch (DataAccessException ex) {
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getActiveLookupData",ex);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for Active LookUp Data count DashBoardBOImpl::getActiveLookupData", ex });
        } finally {
        	getRequestContext().setAdminAware(false);
        }
		return count;
	}

	@Override
	public Long getExpiringLookupData() throws BusinessException,
			SystemException {
		LOGGER.info("Getting the Expiring LookUp Data statistics DashBoardBOImpl::getExpiringLookupData ");
		Integer days = 15;
		Long currentTime = null;  
		Long daysInMillis = null;  
		Long count = null;
		try {
			currentTime = System.currentTimeMillis();
			daysInMillis = TimeUnit.DAYS.toMillis(days);
			count = dashBoardDAO.countExpiringLookUpData(currentTime,currentTime + daysInMillis);
		} catch (DataAccessException ex) {
			LOGGER.error("Error occured in data retreival in DashBoardBOImpl::getExpiringLookupData",ex);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000001, new Object[] { "Unable to get data for Expiring LookUp Data count DashBoardBOImpl::getExpiringLookupData", ex });
        } finally {
        	getRequestContext().setAdminAware(false);
        }
		return count;
	}
}
