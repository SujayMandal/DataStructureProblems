package com.ca.umg.sdc.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryResponseInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateQueryWrapper;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * Syndicate Data extraction controller to fetch syndicate data queries.
 */
@Controller
@RequestMapping("/syndicateDataQueries")
public class SyndicateDataQueriesController {

    @Inject
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;
    
    @Inject
    private VersionDelegate versionDelegate;

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataQueriesController.class);

    /**
     * List all syndicate data queries
     * 
     * @return list of SyndicateDataQueryInfo objects
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<SyndicateDataQueryInfo>> findAllSyndicateDataQueries() {
        RestResponse<List<SyndicateDataQueryInfo>> response = new RestResponse<List<SyndicateDataQueryInfo>>();
        List<SyndicateDataQueryInfo> allQueries = new ArrayList<>();
        try {
            allQueries = getSyndicateDataQueryDelegate().listAll();
            response.setError(false);
            response.setMessage(RestConstants.SD_QUERIES_FETCH_SUCCESS);
            response.setResponse(allQueries);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> saveSyndicateDataQueries(@RequestBody SyndicateDataQueryInfo synDataQryInfo) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            syndicateDataQueryDelegate.createSyndicateDataQuery(synDataQryInfo);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(RestConstants.SYNDICATE_QUERY_CREATE_SUCCESS);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/testQuery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateDataQueryResponseInfo> validateExecSyndicateDataQuery(
            @RequestBody SyndicateDataQueryInfo synDataQryInfo) {
        RestResponse<SyndicateDataQueryResponseInfo> response = new RestResponse<SyndicateDataQueryResponseInfo>();
        try {
            SyndicateDataQueryResponseInfo qryResponse = syndicateDataQueryDelegate.fetchQueryTestData(synDataQryInfo);

            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(qryResponse);
            if (qryResponse != null) {
                LOGGER.error("qryResponse getQueryResponse size:" + qryResponse.getQueryResponse().size());
            }
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }

        return response;
    }

    @RequestMapping(value = "/updateSequence", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> updateExecutionSequence(@RequestBody List<SyndicateDataQueryInfo> queries) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            syndicateDataQueryDelegate.updateExecutionSequence(queries);
            response.setError(false);
            response.setMessage(RestConstants.UPDATE_SEQ_SUCCESS);
            response.setResponse(RestConstants.UPDATE_SEQ_SUCCESS);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> updateSyndicateDataQueries(@RequestBody SyndicateDataQueryInfo synDataQryInfo) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            syndicateDataQueryDelegate.updateSyndicateDataQuery(synDataQryInfo);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(RestConstants.SYNDICATE_QUERY_CREATE_SUCCESS);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    /**
     * List by mapping name
     * 
     * @return list of SyndicateDataQueryInfo objects
     */
    @RequestMapping(value = "/list/{mappingName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateQueryWrapper> findQueriesByMappingName(@PathVariable("mappingName") String mappingName) {
        RestResponse<SyndicateQueryWrapper> response = new RestResponse<SyndicateQueryWrapper>();
        List<SyndicateDataQueryInfo> allQueries = new ArrayList<>();
        SyndicateQueryWrapper syndicateQueryWrapper=new SyndicateQueryWrapper();
        
        try {
        	
        	//UMG-2237
        	boolean publishedOrDeactivated=false;
        	List<Version> versionList=new ArrayList<>();
        	versionList=versionDelegate.findVersionWithTidNameAndStatusPublishedORDeactivated(mappingName);
        	if(versionList!=null){
        		for(Version version : versionList ){
        			if(version.getStatus().equalsIgnoreCase(VersionStatus.PUBLISHED.getVersionStatus()) ||
        					version.getStatus().equalsIgnoreCase(VersionStatus.DEACTIVATED.getVersionStatus())){
        				publishedOrDeactivated=true;
        				break;
        			}
        		}
        	}
        	
        	syndicateQueryWrapper.setPublishedOrDeactivated(publishedOrDeactivated);
            allQueries = getSyndicateDataQueryDelegate().listByMappingName(mappingName);
            syndicateQueryWrapper.setAllQueries(allQueries);
            response.setError(false);
            response.setMessage(RestConstants.SD_QUERIES_FETCH_SUCCESS);
            response.setResponse(syndicateQueryWrapper);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    public SyndicateDataQueryDelegate getSyndicateDataQueryDelegate() {
        return syndicateDataQueryDelegate;
    }
}
