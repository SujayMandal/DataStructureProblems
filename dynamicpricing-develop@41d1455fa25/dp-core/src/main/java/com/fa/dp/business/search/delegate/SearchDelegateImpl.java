package com.fa.dp.business.search.delegate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.db.client.HubzuDBClient;
import com.fa.dp.business.db.client.PMIInscCompsDBClient;
import com.fa.dp.business.db.client.PMIRRDBClient;
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.search.info.FutureReductionSearchDetails;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.ssinvestor.bo.SpclServicingInvestorBO;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Named
@Slf4j
public class SearchDelegateImpl implements SearchDelegate {
	
	public static final String PMI = "PMI";
	
	public static final String SS = "SS";
	
	public static final String NO = "N";

    @Inject
    private CacheManager cacheManager;

    @Inject
    private HubzuDBClient hubzuDBClient;

    @Inject
    private DPProcessParamsBO dpProcessParamsBo;
    
    @Inject
    private DPSopProcessBO dpSopProcessBO;

    @Inject
    private PMIRRDBClient pmiRRDbClient;

    @Inject
    private PMIInscCompsDBClient pmiInscCompsDBClient;

    @Inject
    private SpclServicingInvestorBO spclServicingInvestorBo;
    
    @Inject
	@Named(value = "rrDataSource")
	private DataSource dataSource;
    
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
	public void initializeTemplate() {
    	jdbcTemplate = new JdbcTemplate(dataSource);
	}
    
    @Override
    public FutureReductionSearchDetails getFutureReductionDetails(final String assetNumber, final String oldAssetNumber, final String propTemp, final String occupancy) throws SystemException, ExecutionException, InterruptedException {
        FutureReductionSearchDetails futureReductionSearchDetails = null;
        Long startTime;

        Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
        hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
                (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_FUTURE_REDUCTION_SEARCH_QUERY));
        hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE,
                IntegrationType.FUTURE_REDUCTION_SEARCH_QUERY.getIntegrationType());
        String oldPropTemp = null;
        startTime = DateTime.now().getMillis();
        HubzuDBResponse hubzuDBResponse = hubzuDBClient.fetchAllRowsOfSearch(propTemp, oldAssetNumber, oldPropTemp, hubzuQuery);
        log.info("Time taken for hubzu query for future reduction  : " + (DateTime.now().getMillis() - startTime) + "ms");

        if(CollectionUtils.isNotEmpty(hubzuDBResponse.getHubzuInfos())) {
            futureReductionSearchDetails = populateFutureReductionSearchDetails(hubzuDBResponse, assetNumber, occupancy, propTemp, oldPropTemp );
        }

        return futureReductionSearchDetails;
    }

    private FutureReductionSearchDetails populateFutureReductionSearchDetails(HubzuDBResponse hubzuDBResponse, String assetNumber, String occupancy, String propTemp, String OldPropTemp) throws SystemException, ExecutionException, InterruptedException {
        FutureReductionSearchDetails futureReductionSearchDetails = new FutureReductionSearchDetails();
        Boolean priceChanged = Boolean.FALSE;
        Boolean futureReduction = false;
        setFutureReductionOnPropSoldDt(hubzuDBResponse, propTemp, OldPropTemp, futureReductionSearchDetails );
        futureReductionSearchDetails.setListStatus(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc() == null ?
                "Active" : hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc());
        futureReductionSearchDetails.setListEndDate(hubzuDBResponse.getHubzuInfos().get(0).getListEndDateDtNn() != null ? new SimpleDateFormat(DateConversionUtil.DATE_YYYY_DD_MM).format(hubzuDBResponse.getHubzuInfos().get(0).getListEndDateDtNn()) : null);
        futureReductionSearchDetails.setState(hubzuDBResponse.getHubzuInfos().get(0).getPropStatIdVcFk());
        //lastreduction to be added
       for (int index = 0; index < hubzuDBResponse.getHubzuInfos().size() - 1; index++) {
           if (!priceChanged) {
               if ((hubzuDBResponse.getHubzuInfos().get(index + 1).getListPrceNt() - hubzuDBResponse.getHubzuInfos().get(index).getListPrceNt()) > 0) {
                   futureReductionSearchDetails.setLastReductionDate(hubzuDBResponse.getHubzuInfos().get(index).getListStrtDateDtNn() != null ? new SimpleDateFormat(DateConversionUtil.DATE_YYYY_DD_MM).format(hubzuDBResponse.getHubzuInfos().get(index).getListStrtDateDtNn()) : null);
                   priceChanged = Boolean.TRUE;
                   break;
                   }
               }
           }
       if (!priceChanged)
           futureReductionSearchDetails.setLastReductionDate(hubzuDBResponse.getHubzuInfos().get(hubzuDBResponse.getHubzuInfos().size() - 1).getListStrtDateDtNn() != null ?
                   new SimpleDateFormat(DateConversionUtil.DATE_YYYY_DD_MM).format(hubzuDBResponse.getHubzuInfos().get(hubzuDBResponse.getHubzuInfos().size() - 1).getListStrtDateDtNn()) : null);

       futureReductionSearchDetails.setSopFlag(hubzuDBResponse.getHubzuInfos().get(0).getOccpncySttsAtLstCreatn());
       futureReductionSearchDetails.setListType(hubzuDBResponse.getHubzuInfos().get(0).getListTypeIdVcFk());
        
       //sspmi
       List<PmiInsuranceCompany> pmiInscCompanies = spclServicingInvestorBo.findPmiInsCompsByActiveTrue();
       List<String> pmiInscCompIds = pmiInscCompanies.stream().map(a -> new String[] { a.getInsuranceCompany(), a.getCompanyCode() })
				.flatMap(a -> Arrays.stream(a)).distinct().collect(Collectors.toList());
       String rrClassificationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_CLASSIFICATION_QUERY);
       futureReductionSearchDetails.setSsPmiFlag(jdbcTemplate.execute(rrClassificationQuery, (PreparedStatementCallback<String>) ps -> {
			log.info("Inside doInPreparedStatement.");
			try {
				ps.setString(1, hubzuDBResponse.getHubzuInfos().get(0).getSelrPropIdVcNn());
				ResultSet rs = ps.executeQuery();
				if (null != rs && rs.next()) {
					if (rs.getInt(RRClassificationAggregator.PMI_FLAG) == 1){
						return PMI;
					} else if (StringUtils.equalsIgnoreCase(rs.getString(RRClassificationAggregator.SPECIAL_SERVICING_FLAG), RAClientConstants.YES)){
						return SS;
					} else if(StringUtils.isNotEmpty(rs.getString(RRClassificationAggregator.INSURANCE_COMPANY_ID)) && 
							pmiInscCompIds.contains(rs.getString(RRClassificationAggregator.INSURANCE_COMPANY_ID))) {
						return PMI;
					}
				}
			} catch (SQLException sqle) {
				log.info(sqle.getLocalizedMessage(), sqle);
			}
			return NO;
		}));

        //week0 run date
        //assignemnt from week0
        long listingsCount = hubzuDBResponse.getHubzuInfos().size();
        
        if(DPAConstants.VACANT.equalsIgnoreCase(occupancy)){
        	DPProcessParam dpProcessParams = dpProcessParamsBo.findInWeek0ForAssetNumber(assetNumber);
        	if (!Objects.isNull(dpProcessParams)) {
        		futureReductionSearchDetails.setWeek0RunDate(DateConversionUtil.getUTCDate(dpProcessParams.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
        		futureReductionSearchDetails.setWeek0Assignment(dpProcessParams.getAssignment());
        		futureReductionSearchDetails.setWeek0Eligibility(dpProcessParams.getEligible());
        		listingsCount = hubzuDBResponse.getHubzuInfos().stream()
        				.filter(item -> !"Y".equals(item.getOccpncySttsAtLstCreatn()))
        				.filter(item -> {
        					DateTime assignmentDate = DateConversionUtil.getEstDate(dpProcessParams.getAssignmentDate());
        					DateTime listStartDate = new DateTime(item.getListStrtDateDtNn());
        					if((listStartDate.getYear() > assignmentDate.getYear()) ||
        							((listStartDate.getYear() == assignmentDate.getYear()) &&
        									(listStartDate.getDayOfYear() >= assignmentDate.getDayOfYear())))
        						return true;
        					else
        						return false;
        				})
        				/*.filter(item ->  !item.getListSttsDtlsVc().equals("SUCCESSFUL"))
                        .filter(item ->  !item.getListSttsDtlsVc().equals("UNDERREVIEW"))*/
        				.count();
        	} else {
        		futureReductionSearchDetails.setWeek0RunDate("Not Run");
        	}
        } else if(DPAConstants.OCCUPIED.equalsIgnoreCase(occupancy)) {
        	DPSopWeek0Param dpProcessParams = dpSopProcessBO.findInSOPWeek0ForAssetNumber(assetNumber);
        	if (!Objects.isNull(dpProcessParams)) {
        		futureReductionSearchDetails.setWeek0RunDate(DateConversionUtil.getUTCDate(dpProcessParams.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
        		futureReductionSearchDetails.setWeek0Assignment(dpProcessParams.getAssignment());
        		futureReductionSearchDetails.setWeek0Eligibility(dpProcessParams.getEligible());
        		listingsCount = hubzuDBResponse.getHubzuInfos().stream()
        				.filter(item -> !"N".equals(item.getOccpncySttsAtLstCreatn()))
        				.filter(item -> {
        					DateTime assignmentDate = DateConversionUtil.getEstDate(dpProcessParams.getAssignmentDate());
        					DateTime listStartDate = new DateTime(item.getListStrtDateDtNn());
        					if((listStartDate.getYear() > assignmentDate.getYear()) ||
        							((listStartDate.getYear() == assignmentDate.getYear()) &&
        									(listStartDate.getDayOfYear() >= assignmentDate.getDayOfYear())))
        						return true;
        					else
        						return false;
        				}).count();
        	} else {
        		futureReductionSearchDetails.setWeek0RunDate("Not Run");
        	}
        }
        //no oflistings
        futureReductionSearchDetails.setListingCount(listingsCount);
        futureReductionSearchDetails.setPartOfDp(DPAConstants.NO);
        //future reductions
        if(hubzuDBResponse.getHubzuInfos().get(0).getSelrAcntIdVcFk().equals("900")){
        	futureReductionSearchDetails.setClassification(DPProcessParamAttributes.NRZ.getValue());
            futureReduction = futureReductionSearchDetails.getFutureReductionFlag() == null  ? getFutureReduction(futureReductionSearchDetails, occupancy) : futureReduction;
        } else if(hubzuDBResponse.getHubzuInfos().get(0).getSelrAcntIdVcFk().equals("000")){
        	futureReductionSearchDetails.setClassification(DPProcessParamAttributes.OCN.getValue());
            futureReduction = futureReductionSearchDetails.getFutureReductionFlag() == null  ? getFutureReductionOCN(futureReductionSearchDetails, occupancy) : futureReduction;
        } else {
        	futureReductionSearchDetails.setClassification(DPProcessParamAttributes.PHH.getValue());
            futureReduction = futureReductionSearchDetails.getFutureReductionFlag() == null  ? getFutureReductionOCN(futureReductionSearchDetails, occupancy) : futureReduction;
        }

        if(  futureReduction == Boolean.TRUE && !StringUtils.equalsIgnoreCase(futureReductionSearchDetails.getState(), "PR")) {
            futureReductionSearchDetails.setFutureReductionFlag(DPAConstants.YES);
            futureReductionSearchDetails.setPartOfDp(DPAConstants.YES);
        } else {
            futureReductionSearchDetails.setFutureReductionFlag(DPAConstants.NO);
        }
        return futureReductionSearchDetails;
    }

    private Boolean getFutureReductionOCN(FutureReductionSearchDetails futureReductionSearchDetails, String occupancy) {
    	if(!StringUtils.equalsIgnoreCase("Eligible", futureReductionSearchDetails.getWeek0Eligibility())) {
            setFutureReductionReason(futureReductionSearchDetails,"Week 0 Eligibility",futureReductionSearchDetails.getWeek0Eligibility());
            return Boolean.FALSE;
        } else if(!StringUtils.equalsIgnoreCase("Modeled", futureReductionSearchDetails.getWeek0Assignment())) {
            setFutureReductionReason(futureReductionSearchDetails,"Assignment",futureReductionSearchDetails.getWeek0Assignment());
            return Boolean.FALSE;
        } else if(StringUtils.equalsIgnoreCase("Not Run",futureReductionSearchDetails.getWeek0RunDate())) {
            setFutureReductionReason(futureReductionSearchDetails,"Week 0 Run Date",futureReductionSearchDetails.getWeek0RunDate());
            return Boolean.FALSE;
        } else if(futureReductionSearchDetails.getListingCount() > 12) {
            setFutureReductionReason(futureReductionSearchDetails,"No. Of Listings",futureReductionSearchDetails.getListingCount().toString());
            return Boolean.FALSE;
        }

        return getFutureReduction(futureReductionSearchDetails, occupancy);
    }
    private Boolean getFutureReduction(FutureReductionSearchDetails futureReductionSearchDetails, String occupancy) {
        if("Y".equals(futureReductionSearchDetails.getSopFlag()) && "Vacant".equals(occupancy)) {
            setFutureReductionReason(futureReductionSearchDetails,"Latest SOP Flag",futureReductionSearchDetails.getSopFlag());
            return Boolean.FALSE;
        } else if("N".equals(futureReductionSearchDetails.getSopFlag()) && "SOP".equals(occupancy)) {
            setFutureReductionReason(futureReductionSearchDetails,"Latest SOP Flag",futureReductionSearchDetails.getSopFlag());
            return Boolean.FALSE;
        } else if(!NO.equals(futureReductionSearchDetails.getSsPmiFlag())) {
            setFutureReductionReason(futureReductionSearchDetails,"SS / Pmi Flag",futureReductionSearchDetails.getSsPmiFlag());
            return Boolean.FALSE;
        } else if(!"AUCN".equals(futureReductionSearchDetails.getListType())) {
            setFutureReductionReason(futureReductionSearchDetails,"Latest List Type",futureReductionSearchDetails.getListType());
            return Boolean.FALSE;
        } else if(futureReductionSearchDetails.getListStatus().equals("SUCCESSFUL") || futureReductionSearchDetails.getListStatus().equals("UNDERREVIEW")) {
            setFutureReductionReason(futureReductionSearchDetails,"Latest List Status",futureReductionSearchDetails.getListStatus());
            futureReductionSearchDetails.setPartOfDp(DPAConstants.YES);
            return Boolean.FALSE;
        }
        return true;
    }

    private void setFutureReductionOnPropSoldDt(HubzuDBResponse hubzuDBResponse,String propTemp, String OldPropTemp, FutureReductionSearchDetails futureReductionSearchDetails ) {

        HubzuInfo newObj = hubzuDBResponse.getHubzuInfos().stream().filter(HubzuInfo -> ((HubzuInfo.getRbidPropIdVcFk().equals(DPAConstants.NRZ_ACNT_ID + propTemp) ||
                HubzuInfo.getRbidPropIdVcFk().equals(DPAConstants.OCN_ACNT_ID + propTemp)) && HubzuInfo.getPropSoldDateDt() != null )).findFirst().orElse(null);
        futureReductionSearchDetails.setSoldDate( newObj != null ? newObj.getPropSoldDateDt() : null );
        if(futureReductionSearchDetails.getSoldDate() == null) {
            HubzuInfo oldObj = hubzuDBResponse.getHubzuInfos().stream().filter(HubzuInfo -> ((HubzuInfo.getRbidPropIdVcFk().equals(DPAConstants.NRZ_ACNT_ID + OldPropTemp) ||
                    HubzuInfo.getRbidPropIdVcFk().equals(DPAConstants.OCN_ACNT_ID + OldPropTemp)) && HubzuInfo.getPropSoldDateDt() != null )).findFirst().orElse(null);
            futureReductionSearchDetails.setSoldDate( oldObj != null ? oldObj.getPropSoldDateDt() : null );
}
        if(futureReductionSearchDetails.getSoldDate() != null) {
            futureReductionSearchDetails.setFutureReductionFlag(DPAConstants.YES);
            futureReductionSearchDetails.setPartOfDp(DPAConstants.NO);
            setFutureReductionReason(futureReductionSearchDetails,"Sold Date", futureReductionSearchDetails.getSoldDate());
        }
    }

    private void setFutureReductionReason(FutureReductionSearchDetails futureReductionSearchDetails, String rfield, String rValue) {
       Map<String,String> reasonMap = new HashMap<String,String>();
        reasonMap.put(rfield,rValue);
        futureReductionSearchDetails.setReason(reasonMap);
    }

}
