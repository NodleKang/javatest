package com.lgcns.test.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MyQueueService {

    private String name;

    private ConcurrentLinkedQueue<Message> queues;

    private int size;

    private long processTimeout = 300; // �� �� �ִ� �ð�����(300��)

    private int maxFailCount = 3; // ���� Ƚ��

    private int waitTime = 10; // 10��

    private boolean isDLQ = false; // DLQ ����

    public MyQueueService(String queueName, int size, int processTimeout, int maxFailCount, int waitTime, boolean isDLQ) {
        this.name = queueName;
        this.queues = new ConcurrentLinkedQueue<>();
        this.size = size;
        this.processTimeout = processTimeout;
        this.maxFailCount = maxFailCount;
        this.waitTime = waitTime;
        this.isDLQ = isDLQ;
    }

    /**
     * ť�� ����ִ� �޽��� ������ ��ȯ�ϱ�
     */
    public int getQueueSize() {
        return queues.size();
    }

    public int getWaitTime() {
        return waitTime;
    }

    /**
     * ť�� �޽��� �߰��ϱ�
     */
    public String addMessage(Message message) {
        if (queues.size() >= size) {
            // ť�� ũ�Ⱑ �ִ� ũ�⺸�� ũ�� �޽����� ���� �ʰ� ����
            return "Queue Full";
        }
        // ť�� �� �޽��� �߰�
        queues.add(message);
        return "Ok";
    }

    /**
     * ť�� �޽��� �߰��ϱ�
     */
    public String addMessage(String msgBody) {
        if (queues.size() >= size) {
            // ť�� ũ�Ⱑ �ִ� ũ�⺸�� ũ�� �޽����� ���� �ʰ� ����
            return "Queue Full";
        }
        // ť�� �� �޽��� �߰�
        String msgId = UUID.randomUUID().toString();
        Message msg = new Message(msgId, msgBody);
        queues.add(msg);
        System.out.println("[addMessage]" + System.currentTimeMillis() + " : " + name + " : " + msgBody + " : " + msgId);
        return "Ok";
    }

    /**
     * �Ϲ� ť�� �޽��� ��������
     */
    public String popMessage() {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues.size() == 0) {
            result.put("Result", "No Message");
        }

        // epoc_millis ����ð�
        long nowTime = now();

        // ���� �� �� �ִ� �޽����̰�,
        // ProcessTimeout �ð��� ������ �ʾҰ�,
        // ���� Ƚ���� �ִ� ���� Ƚ������ ���� �޽��� �߿� ù���� �޽��� ã��
        Optional<Message> msgOpt = queues
                .stream()
                .filter(
                        m -> m.isVisible() && !m.isProcessTimeout(nowTime) && m.getFailures() <= maxFailCount
                )
                .findFirst();
        if (msgOpt.isEmpty()) {
            result.put("Result", "No Message");
        } else {
            Message msg = msgOpt.get();
            msg.setVisible(false);
            result.put("Result", "Ok");
            result.put("MessageID", msg.getMsgId());
            result.put("Message", msg.getMsgBody());
            if (processTimeout > 0) {
                msg.setProcessTimeoutFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));
            }
            System.out.println("[popMessage]" + System.currentTimeMillis() + " : " + name + " : " + msg.getMsgBody() + " : " + msg.getMsgId());
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(result);
    }

    public String popMessageDLQ() {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        Message msg = queues.poll();

        if (msg == null) {
            result.put("Result", "No Message");
        } else {
            result.put("Result", "Ok");
            result.put("MessageID", msg.getMsgId());
            result.put("Message", msg.getMsgBody());
            System.out.println("[popMessageDLQ]" + name + " : " + msg.getMsgBody() + " : " + msg.getMsgId());
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(result);
    }

    /**
     * ť���� �޽��� ����
     * @param msgId
     * @return
     */
    public String removeMessage(String msgId) {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues != null) {

            // epoch_millis ����ð�
            long nowTime = now();

            // ť�� ������ ���鼭
            for (Iterator<Message> it = queues.iterator(); it.hasNext();) {
                // �޽����� �����´�
                Message msg = it.next();
                // �޽��� ���̵� �Էµ� ���̵�� ������ ť���� �ش� �޽����� �����Ѵ�
                if (msg.getMsgId().equals(msgId)) {
                    it.remove();
                    System.out.println("[removeMessage]" + System.currentTimeMillis() + " : " + name + " : " + msg.getMsgBody() + " : " + msg.getMsgId());
                    result.put("Result", "Ok");
                    break;
                }
            }

        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(result);
    }

    /**
     * ť�� �޽��� ����
     * @param msgId
     * @return
     */
    public String restoreMessage(String msgId) {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues != null) {

            // epoch_millis ����ð�
            long nowTime = now();

            // ť�� ������ ���鼭
            for (Iterator<Message> it = queues.iterator(); it.hasNext();) {
                // �޽����� �����´�
                Message msg = it.next();
                // ���� Ƚ���� �ִ� ���� Ƚ������ �۰� &&
                // �޽��� ���̵� �Էµ� ���̵�� ������ ť���� �ش� �޽����� �����Ѵ�.
                if (msg.getFailures() <= maxFailCount && msg.getMsgId().equals(msgId)) {
                    msg.incrementFailures();
                    msg.setProcessTimeoutFrom(0L);
                    msg.setVisible(true);
                    System.out.println("[restoreMessage]" + name + " : " + msg.getMsgBody() + " : " + msg.getMsgId() + " : " + msg.getFailures());
                    result.put("Result", "Ok");
                    break;
                }
            }

        }

        System.out.println("[restoreMessage] " + " : " + name + " : " + getQueueSize());
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(result);
    }

    /**
     * ProcessTimeout�� ���� �޽����� ó���ϱ�
     */
    public void processTimeoutMessages() {

        // epoc_millis ����ð�
        long nowTime = now();

        // ���� processTimeout�� ���� �޽��� ó���ϱ�
        for (Message m: queues) {
            if (m.isProcessTimeout(nowTime)) {
                // �޽��� �����ϱ� (�޼ҵ� �ȿ��� ����Ƚ���� ������Ű��, processTimeout�� 0���� �����)
                restoreMessage(m.getMsgId());
            }
        }
    }

    /**
     * ���� Ƚ���� �ִ� ���� Ƚ���� ���� �޽��� ó���ϱ�
     */
    public void processOverFailedMessages() {

        // ���� ���� Ƚ���� �ִ� ���� Ƚ���� ���� �޽��� ó���ϱ�
        for (Message m : queues) {
            if (m.getFailures() > maxFailCount) {
                // �޽����� DLQ�� �̵���Ű��
                moveMessageToDLQ(m);
            }
        }

    }

    /**
     * �޽����� DLQ�� �̵���Ű��
     */
    private void moveMessageToDLQ(Message m) {
        System.out.println("[moveMessageToDLQ] before move " + System.currentTimeMillis() + " : " + name + " : " + getQueueSize());
        // �⺻ ť���� �޽��� ����
        queues.remove(m);
        // DLQ�� ť�� �޽��� �߰�
        MyQueueManager.getInstance().getQueueService(name+"-DLQ").addMessage(m);
        System.out.println("[moveMessageToDLQ] after move " + System.currentTimeMillis() + " : " + name + " : " + getQueueSize());
    }

    // epoch_mills ���� �ð�
    long now() {
        return System.currentTimeMillis();
    }
}