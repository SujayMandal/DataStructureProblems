/**
 * 
 */
package com.ca.umg.rt.batching.aggregator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.integration.Message;

/**
 * The agrregator is used for sequencing the responses in the same order as the requests were.
 * 
 * @author chandrsa
 * 
 */
public class BatchAggregator {

    public Object aggregate(List<Message<?>> messages) {
        List<Object> payloads = null;
        Map<Integer, Object> payloadSeqMap = new TreeMap<Integer, Object>();

        for (Message<?> message : messages) {
            payloadSeqMap.put(message.getHeaders().getSequenceNumber(), message.getPayload());
        }
        payloads = new LinkedList<>(payloadSeqMap.values());
        return payloads;
    }
}
