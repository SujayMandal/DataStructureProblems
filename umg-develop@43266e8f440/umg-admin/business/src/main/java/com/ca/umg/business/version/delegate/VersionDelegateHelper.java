package com.ca.umg.business.version.delegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionHierarchyInfo;
import com.ca.umg.business.version.info.VersionInfo;

import ma.glasnost.orika.impl.ConfigurableMapper;

@Named
public class VersionDelegateHelper {

    @Inject
    private ConfigurableMapper mapper;

    public List<VersionHierarchyInfo> createVersionHierarchy(List<Version> versions) {
        String libraryName = null;
        String modelName = null;
        Map<String, Set<String>> validator = new HashMap<>();
        List<VersionHierarchyInfo> hierarchyInfos = new ArrayList<>();
        VersionHierarchyInfo modelLibraryHierInfo = null;
        VersionHierarchyInfo modelHierInfo = null;
        VersionInfo versionInfo = null;
        Set<String> models = null;
        for (Version version : versions) {
            libraryName = version.getModelLibrary().getName();
            modelName = version.getMapping().getModel().getName();
            versionInfo = mapper.map(version, VersionInfo.class);
            if (validator.containsKey(libraryName)) {
                if (validator.get(libraryName).contains(modelName)) {
                    if(modelHierInfo == null) {
                    	modelHierInfo = new VersionHierarchyInfo();
                    }
                	modelHierInfo.addVersionInfo(versionInfo);
                } else {
                    validator.get(libraryName).add(modelName);
                    modelHierInfo = new VersionHierarchyInfo();
                    modelHierInfo.setName(modelName);
                    modelHierInfo.addVersionInfo(versionInfo);
                    if(modelLibraryHierInfo == null) {
                    	modelLibraryHierInfo = new VersionHierarchyInfo();
                    }
                    modelLibraryHierInfo.addVersionHierarchy(modelHierInfo);
                }
            } else {
                modelLibraryHierInfo = new VersionHierarchyInfo();
                modelLibraryHierInfo.setName(version.getModelLibrary().getName());
                hierarchyInfos.add(modelLibraryHierInfo);
                modelHierInfo = new VersionHierarchyInfo();
                modelHierInfo.setName(modelName);
                modelHierInfo.addVersionInfo(versionInfo);
                modelLibraryHierInfo.addVersionHierarchy(modelHierInfo);
                models = new HashSet<>();
                models.add(modelName);
                validator.put(libraryName, models);
            }
        }
        return hierarchyInfos;
    }

}
