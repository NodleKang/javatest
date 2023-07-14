package com.lgcns.test.servlet;

import com.google.gson.annotations.SerializedName;

public class JsonQueueConfig {
    @SerializedName("QueueSize")
    private Integer queueSize;
    @SerializedName("ProcessTimeout")
    private Integer processTimeout;
    @SerializedName("MaxFailCount")
    private Integer maxFailCount;
    @SerializedName("WaitTime")
    private Integer waitTime;

    public Integer getQueueSize() { return queueSize != null ? queueSize : 0; }
    public void setQueueSize(Integer value) { this.queueSize = value; }

    public Integer getProcessTimeout() { return processTimeout != null ? processTimeout : 0; }
    public void setProcessTimeout(Integer value) { this.processTimeout = value; }

    public Integer getMaxFailCount() { return maxFailCount != null ? maxFailCount : 0; }
    public void setMaxFailCount(Integer value) { this.maxFailCount = value; }

    public Integer getWaitTime() { return waitTime != null ? waitTime : 0; }
    public void setWaitTime(Integer value) { this.waitTime = value; }
}
