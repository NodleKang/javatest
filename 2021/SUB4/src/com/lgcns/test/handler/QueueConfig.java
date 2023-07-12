package com.lgcns.test.handler;

public class QueueConfig {
    private int queueSize;
    private int processTimeout;
    private int maxFailCount;
    private int waitTime;

    public int getQueueSize() { return queueSize; }
    public void setQueueSize(int value) { this.queueSize = value; }

    public int getProcessTimeout() { return processTimeout; }
    public void setProcessTimeout(int value) { this.processTimeout = value; }

    public int getMaxFailCount() { return maxFailCount; }
    public void setMaxFailCount(int value) { this.maxFailCount = value; }

    public int getWaitTime() { return waitTime; }
    public void setWaitTime(int value) { this.waitTime = value; }
}
