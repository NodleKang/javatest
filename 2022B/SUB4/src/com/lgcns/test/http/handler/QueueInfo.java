package com.lgcns.test.http.handler;

import java.util.List;

public class QueueInfo {

    private int processCount;
    private int threadCount;
    private int outputQueueBatchSize;
    private int inputQueueCount;
    private List<String> inputQueueURIs;
    private String outputQueueURI;

    public int getProcessCount() {
        return processCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getOutputQueueBatchSize() {
        return outputQueueBatchSize;
    }

    public int getInputQueueCount() {
        return inputQueueCount;
    }

    public List<String> getInputQueueURIs() {
        return inputQueueURIs;
    }

    public String getOutputQueueURI() {
        return outputQueueURI;
    }

}
