package com.ca.umg.business.accessprivilege.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.AccessPrivilege;
import com.ca.umg.business.accessprivilege.dao.AccessPrivilegeDAO;

@Named
public class AccessPrivilegeBOImpl implements AccessPrivilegeBO {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessPrivilegeBOImpl.class);

	@Inject
	private AccessPrivilegeDAO accessPrivilegeDAO;

	@Inject
	private AccessPrivilege accessPrivilege;

	@PostConstruct
	public void setPrivileges() throws BusinessException {
		try {
			accessPrivilegeDAO.setPrivileges(accessPrivilege);
		} catch (BusinessException e) {
			LOGGER.error("Error occured while getting the privileges : ", e);
			throw e;
		}
	}

	@Override
	public Map<String, Object> getRolesPrivilegesMap(String tenantCode) throws SystemException {
		Map<String, Object> result = null;
		try {
			result = new TreeMap<>(accessPrivilegeDAO.rolePrivilegeMapping(tenantCode));
		} catch (SystemException e) {
			LOGGER.error("Error occured while getting role privilege map : {}", e);
			throw e;
		}
		return result;

	}

	@Override
	@Transactional
	public void setRolesPrivilegesMap(String tenantCode, String role, List<String> prvilegeAsList)
			throws SystemException, SQLException {
		try {
			accessPrivilegeDAO.setrolePrivilegeMapping(tenantCode, role, prvilegeAsList);
		} catch (SystemException e) {
			LOGGER.error("Error occured while setting value to privilege object : {}", e);
			throw e;
		}
	}
}
