/**
 * 
 */
package com.ca.umg.business.transaction.query;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kamathan
 * 
 */
public enum Operator {
    OR("$or","||"),

    AND("$and","&&"),

    GREATER_THAN("$gt",">"),

    GREATER_THAN_EQUAL("$gte",">="),

    LESS_THAN("$lt","<"),

    LESS_THAN_EQUAL("$lte","<="),

    IN("$in","IN"),

    EQUAL(":","="),

    NOT_EQUAL("$ne","!="),

    LIKE("$regex","LIKE");

    private String operator;
    private String operatorValue;

    private Operator(String operator, String displayValue) {
        this.operator = operator;
        this.operatorValue = displayValue;
    }

    public String getOperator() {
        return operator;
    }
    
    public String getOperatoreValue() {
    	return operatorValue;
    }
    
    private static Map<String, Operator> codeValueMap = new HashMap<String, Operator>();

    static
    {
        for (Operator  type : Operator.values())
        {
            codeValueMap.put(type.operatorValue, type);
        }
    }

    //constructor and getCodeValue left out      

    public static Operator getInstanceFromCodeValue(String operatrValue)
    {
        return codeValueMap.get(operatrValue);
    }
}
