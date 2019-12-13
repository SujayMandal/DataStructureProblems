package com.ca.umg.business.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Required;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.dao.ModelImplementationTypeDAO;
import com.ca.umg.business.model.entity.ModelImplementationType;

public class SchemaLoader {

    private Map<String, String> schemaMap;

    private final Map<String, byte[]> map = new HashMap<String, byte[]>();

    @Inject
    private ModelImplementationTypeDAO modelImplementationTypeDAO;

    @Required
    public void setSchemaMap(Map<String, String> map) {
        this.schemaMap = map;
    }

    @PostConstruct
    public void persistModelXSD() throws SystemException {
        modelImplementationTypeDAO.deleteAll();
        for (String implementation : schemaMap.keySet()) {
            byte[] schema = AdminUtil.convertStreamToByteArray(ResourceLoader.getResource(schemaMap.get(implementation)));
            ModelImplementationType implementationType = new ModelImplementationType();
            implementationType.setImplementation(implementation);
            implementationType.setTypeXSD(schema);
            modelImplementationTypeDAO.save(implementationType);
            map.put(implementation, schema);
        }
    }

}
