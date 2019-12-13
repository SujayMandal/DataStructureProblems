package com.ca.umg.business.version.info;

import java.util.ArrayList;
import java.util.List;

public class VersionHierarchyInfo {

    private String name;

    private List<VersionHierarchyInfo> hierarchyInfos;

    private List<VersionInfo> versionInfos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VersionHierarchyInfo> getHierarchyInfos() {
        return hierarchyInfos;
    }

    public void setHierarchyInfos(List<VersionHierarchyInfo> hierarchyInfos) {
        this.hierarchyInfos = hierarchyInfos;
    }

    public List<VersionInfo> getVersionInfos() {
        return versionInfos;
    }

    public void setVersionInfos(List<VersionInfo> versionInfos) {
        this.versionInfos = versionInfos;
    }

    public void addVersionHierarchy(VersionHierarchyInfo versionHierarchyInfo) {
        if (hierarchyInfos == null) {
            hierarchyInfos = new ArrayList<>();
        }
        hierarchyInfos.add(versionHierarchyInfo);
    }

    public void addVersionInfo(VersionInfo versionInfo) {
        if (versionInfos == null) {
            versionInfos = new ArrayList<>();
        }
        versionInfos.add(versionInfo);
    }

}
