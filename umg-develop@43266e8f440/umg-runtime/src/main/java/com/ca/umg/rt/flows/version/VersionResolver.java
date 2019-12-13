package com.ca.umg.rt.flows.version;

import com.ca.framework.core.exception.SystemException;

public interface VersionResolver {

    public void resolve() throws SystemException;
}
