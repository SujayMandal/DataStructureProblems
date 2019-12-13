package com.ca.umg;

import com.ca.exception.RAClientException;

import java.util.Map;

/**
 * Created by repvenk on 9/22/2016.
 */
public interface IRAClient {

    public byte[] execute(byte[] requestBytes) throws RAClientException;

    public Map execute(Map inputMap) throws RAClientException;

}
