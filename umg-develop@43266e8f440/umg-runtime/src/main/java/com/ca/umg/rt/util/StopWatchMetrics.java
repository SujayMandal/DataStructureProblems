package com.ca.umg.rt.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by repvenk on 3/9/2016.
 */
public class StopWatchMetrics {

    private final Map<String, CheckPoint> checkPointMap = new HashMap<>();
    private CheckPoint lastCheckPoint;
    private final boolean trackMetrics;

    private class CheckPoint {
        private long startTime;
        private long endTime;
        private long elapsedTime;
        private final String checkPointDesc;
        private CheckPoint prev;
        private boolean stopped;

        public CheckPoint(String desc) {
            checkPointDesc = desc;
        }

        public void start() {
            startTime = System.currentTimeMillis();
        }

        public long stop() {
            endTime = System.currentTimeMillis();
            elapsedTime = endTime - startTime;
            stopped = Boolean.TRUE;
            return startTime - endTime;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public String getCheckPointDesc() {
            return checkPointDesc;
        }

        public boolean isStopped() {
            return stopped;
        }
    }

    public StopWatchMetrics(boolean trackMetrics) {
        this.trackMetrics = trackMetrics;
    }

    public CheckPoint createCheckPointAndStart(String checkPointDesc) {
        CheckPoint checkPoint = null;
        if(trackMetrics) {
            checkPoint = new CheckPoint(checkPointDesc);
            checkPoint.prev = lastCheckPoint;
            lastCheckPoint = checkPoint;
            checkPoint.start();
            checkPointMap.put(checkPointDesc, checkPoint);
        }
        return checkPoint;
    }

    public void stopCheckPoint(String checkPointDesc) {
        if(trackMetrics) {
            CheckPoint cp = checkPointMap.get(checkPointDesc);
            if (cp.equals(lastCheckPoint)) {
                stopLastCheckPoint();
            } else {
                cp.stop();
            }
        }
    }

    public void stopLastCheckPoint() {
        if(trackMetrics) {
            lastCheckPoint.stop();
            lastCheckPoint = lastCheckPoint.prev;
            while (lastCheckPoint != null && lastCheckPoint.isStopped()) {
                lastCheckPoint = lastCheckPoint.prev;
            }
        }
    }

    public Map<String, Long> getMetrics() {
        Map<String, Long> metricMap = null;
        if(trackMetrics) {
            metricMap = new HashMap<>();
            for (Map.Entry<String, CheckPoint> entry : checkPointMap.entrySet()) {
                metricMap.put(entry.getKey(), entry.getValue().getElapsedTime());
            }
        }
        return metricMap;
    }

}
