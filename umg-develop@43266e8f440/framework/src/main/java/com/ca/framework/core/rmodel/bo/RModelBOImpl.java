package com.ca.framework.core.rmodel.bo;

import com.ca.framework.core.rmodel.dao.RModelDAO;


public class RModelBOImpl implements RModelBO {
	
	private RModelDAO rModelDAO;

	/*@Override
	public String getRModelInfo(String modelName, Integer majorVersion, Integer minorVersion, final String tenantCode) {
		String modelInfo = null;
		Map<String, Object> headerInfo = new LinkedHashMap<String, Object>();
		List<SupportPackage> supportPackage = rModelDAO.getSupportPackageList(modelName, majorVersion, minorVersion, tenantCode);
        headerInfo.put("libraries", supportPackage);
        headerInfo.put("modelPackageName", rModelDAO.getModelPackageName(modelName, majorVersion, minorVersion, tenantCode));
        headerInfo.put("commandName", "load");
        
        Map<String, Object> modelRequest = new LinkedHashMap<String, Object>();
        modelRequest.put("headerInfo", headerInfo);
        modelRequest.put("payload",  new ArrayList<Map<String, Object>>());
        
        Map<String, Object> modelInfoMap = new LinkedHashMap<String, Object>();
        modelInfoMap.put("modelRequest", modelRequest);
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
			modelInfo = objectMapper.writeValueAsString(modelInfoMap);
		} catch (IOException e) {
			// TODO Add the business/system exception
			e.printStackTrace();
		}
		return modelInfo;
	}*/

	public RModelDAO getrModelDAO() {
		return rModelDAO;
	}

	public void setrModelDAO(RModelDAO rModelDAO) {
		this.rModelDAO = rModelDAO;
	}

}
