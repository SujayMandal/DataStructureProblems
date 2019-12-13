package com.ca.framework.core.logging.appender;

import java.util.Collections;

import org.apache.logging.log4j.nosql.appender.NoSqlObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class TenantAwareDbObject implements NoSqlObject<BasicDBObject> {
    private static String CONTEXT_MAP = "contextMap";
    private final BasicDBObject tenantAwareDbObject;

    public TenantAwareDbObject() {
        this.tenantAwareDbObject = new BasicDBObject();
    }

    public void set(String field, Object value) {
        this.tenantAwareDbObject.append(field, value);
    }

    public void set(String field, NoSqlObject<BasicDBObject> value) {
        this.tenantAwareDbObject.append(field, value.unwrap());
    }

    public void set(String field, Object[] values) {
        BasicDBList list = new BasicDBList();
        Collections.addAll(list, values);
        this.tenantAwareDbObject.append(field, list);
    }

    public void set(String field, NoSqlObject<BasicDBObject>[] values) {
        BasicDBList list = new BasicDBList();
        for (NoSqlObject<?> value : values) {
            list.add(value.unwrap());
        }
        this.tenantAwareDbObject.append(field, list);
    }

    public BasicDBObject unwrap() {
        return this.tenantAwareDbObject;
    }
    
    public String getTenant() {
        Object object = tenantAwareDbObject.get(CONTEXT_MAP);
        if(object !=null && object instanceof BasicDBObject){
            return (String)((BasicDBObject)object).get(AppenderConstants.MDC_TENANT_CODE);
        }
        return null;
    }

}
