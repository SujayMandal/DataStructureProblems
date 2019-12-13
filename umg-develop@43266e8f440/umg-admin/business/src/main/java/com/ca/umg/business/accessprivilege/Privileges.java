package com.ca.umg.business.accessprivilege;

import javax.inject.Named;

@Named
public class Privileges {
    
    private String permission;
    
    private String permissionType;
    
    private String uiElementId;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getUiElementId() {
        return uiElementId;
    }

    public void setUiElementId(String uiElementId) {
        this.uiElementId = uiElementId;
    }

}
