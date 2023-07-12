package com.lgcns.test.queue;

import java.util.concurrent.ConcurrentHashMap;

public class MyQueueManager {

    private static MyQueueManager instance;

    private ConcurrentHashMap<String, MyQueueService> queues;

    public static MyQueueManager getInstance() {
        if (instance == null) {
            synchronized (MyQueueManager.class) {
                instance = new MyQueueManager();
                instance.queues = new ConcurrentHashMap<>();
            }
        }
        return instance;
    }

    public boolean createQueueService(String queueName, int size) {
        MyQueueService queueService = queues.get(queueName);
        if (queueService == null) {
            queueService = new MyQueueService(queueName, size);
            queues.put(queueName, queueService);
        }
        return true;
    }

    public MyQueueService getQueueService(String queueName) {
        return queues.get(queueName);
    }

}
