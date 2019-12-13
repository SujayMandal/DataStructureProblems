package com.ca.umg.business.mapping.bo;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.dao.MappingInputDAO;
import com.ca.umg.business.mapping.dao.MappingOutputDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.mapping.specification.MappingListingSpecification;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.dao.VersionContainerDAO;

@Service
public class MappingBOImpl extends AbstractBusinessObject implements MappingBO {
    private static final long serialVersionUID = 1113902024113485655L;

    @Inject
    private MappingDAO mappingDAO;

    @Inject
    private MappingInputDAO mappingInputDAO;

    @Inject
    private MappingOutputDAO mappingOutputDAO;

    @Inject
    private VersionContainerDAO versionContainerDAO;

    // @Inject RestTemplate restTemplate;

    public List<Mapping> listAll() throws SystemException, BusinessException {
        return mappingDAO.findAll(new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_BY));
    }

    public Mapping create(Mapping mapping) throws SystemException, BusinessException {
        validate(mapping);
        return mappingDAO.saveAndFlush(mapping);
    }

    @Override
    public Mapping find(String identifier) throws SystemException, BusinessException {
        return mappingDAO.findOne(identifier);
    }

    @Override
    public MappingInput createMappingInput(MappingInput mappingInput) throws SystemException, BusinessException {
        validate(mappingInput);
        return mappingInputDAO.saveAndFlush(mappingInput);
    }

    @Override
    public MappingOutput createMappingOutput(MappingOutput mappingOutput) throws SystemException, BusinessException {
        validate(mappingOutput);
        return mappingOutputDAO.saveAndFlush(mappingOutput);
    }

    @Override
    public Mapping findByName(String tidName) throws SystemException, BusinessException {
        return mappingDAO.findByName(tidName);
    }

    @Override
    public MappingInput findInputByMapping(Mapping mapping) throws SystemException, BusinessException {
        MappingInput input = null;
        if (mapping != null) {
            input = mappingInputDAO.findByMapping(mapping);
        }
        return input;
    }

    @Override
    public MappingOutput findOutputByMapping(Mapping mapping) throws SystemException, BusinessException {
        MappingOutput output = null;
        if (mapping != null) {
            output = mappingOutputDAO.findByMapping(mapping);
        }
        return output;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public boolean deleteTidMapping(String tidName) throws SystemException, BusinessException {
        MappingOutput mappingOutput = null;
        MappingInput mappingInput = null;
        Mapping mapping = null;
        boolean deleteSuccess = false;
        if (StringUtils.isNotBlank(tidName)) {
            mapping = findByName(tidName);
            if (mapping != null) {
                mappingOutput = findOutputByMapping(mapping);
                mappingInput = findInputByMapping(mapping);

                if (mappingOutput != null) {
                    mappingOutputDAO.delete(mappingOutput);
                }
                if (mappingInput != null) {
                    mappingInputDAO.delete(mappingInput);
                }
                mappingDAO.delete(mapping);
                deleteSuccess = true;
            } else {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000048, new Object[] { tidName });
            }
        }
        return deleteSuccess;
    }

    @Override
    public List<String> getListOfMappingNames(String modelName) throws SystemException, BusinessException {
        return mappingDAO.getListOfMappingNames(modelName);
    }

    @Override
    public List<Mapping> findByModelName(String modelName) throws SystemException, BusinessException {
        return mappingDAO.findByModelName(modelName);
    }

    @Override
    public List<Mapping> findFinalizedMappings(String modelName) throws SystemException, BusinessException {
        return mappingDAO.findByModelNameAndStatus(modelName, MappingStatus.FINALIZED.getMappingStatus());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.mapping.bo.MappingBO#getListOfMappingNamesById(java.lang.String)
     */
    @Override
    public List<String> getListOfMappingNamesById(String modelId) throws SystemException, BusinessException {
        return mappingDAO.getListOfMappingNamesById(modelId);
    }

    /*
     * @Override public List<MappingsCopyInfo> getAllTidsForCopy() { List<String[]> mappingList =
     * mappingDAO.findListOfMappingsForTidCopy(); List<MappingsCopyInfo> mappingCopyInfoList = null; if
     * (CollectionUtils.isNotEmpty(mappingList)) { mappingCopyInfoList = new ArrayList<MappingsCopyInfo>(); for (Object[] rowData
     * : mappingList) { mappingCopyInfoList.add(populateInfo(rowData)); } } return mappingCopyInfoList; }
     */
    @Override
    public List<MappingsCopyInfo> getAllTidsForCopy() throws BusinessException {
        return versionContainerDAO.getDataForTidCopy();
    }

    @Override
    public String getMappingStatus(String tidName) throws SystemException, BusinessException {
        return mappingDAO.getMappingStatus(tidName);
    }

    @Override
    public MappingOutput getMappingOutputByMappingName(String mappingName) throws BusinessException, SystemException {
        return mappingOutputDAO.findByMappingName(mappingName);
    }

    /**
     * This method will retrieve all mapping and return paginated data back with pageinfo
     */
    @Override
    public List<Mapping> findAllMappings(SearchOptions searchOptions) throws BusinessException, SystemException {

        Long fromDate = null;
        Long toDate = null;

        // setting start and end date to Long format
        if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
            fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
            toDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        Specification<Mapping> createFromSpec = MappingListingSpecification.withCreatedDateFrom(fromDate);
        Specification<Mapping> createToSpec = MappingListingSpecification.withCreatedDateTo(toDate);
        Specification<Mapping> createdBySpec = MappingListingSpecification.withCreatedBy(searchOptions.getSearchText());

        Specification<Mapping> tidNameSpec = MappingListingSpecification.withTIDNameLike(searchOptions.getSearchText());
        Specification<Mapping> tidDescSpec = MappingListingSpecification.withTIDDescriptionLike(searchOptions.getSearchText());
        Specification<Mapping> statusSpec = MappingListingSpecification.withStatus(searchOptions.getSearchText());

        return mappingDAO.findAll(
                where(createFromSpec).and(createToSpec).and(where(tidNameSpec).or(tidDescSpec).or(createdBySpec).or(statusSpec)));

    }

}
