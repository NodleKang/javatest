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

    public MyQueueService(String queueName, int size, int processTimeout, int maxFailCount) {
        this.name = queueName;
        this.queues = new ConcurrentLinkedQueue<>();
        this.size = size;
        this.processTimeout = processTimeout;
        this.maxFailCount = maxFailCount;
    }

    /**
     * ť�� ����ִ� �޽��� ������ ��ȯ�ϱ�
     */
    public int getSize() {
        return queues.size();
    }

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
        return "Ok";
    }

    /**
     * ť���� �޽��� �ϳ� ��������
     */
    public String popMessage() {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues.size() == 0) {
            result.put("Result", "No Message");
        }

        // epoc_millis ����ð�
        long nowTime = now();

        // stream�� ����ؼ� ���� Visible �� �޽��� �߿� ù ��° ��� ��������
        // optional�� ����ؼ� null pointer exception�� ����ϱ�
        Optional<Message> msgOpt = queues.stream().filter(m -> m.isVisibleAt(nowTime)).findFirst();
        if (msgOpt.isEmpty()) {
            result.put("Result", "No Message");
        } else {
            // �޽����� ã����
            Message msg = msgOpt.get();
            // �޽����� ���� �õ� Ƚ���� ������Ų��
            msg.incrementAttempts();
            // ecpoch_millis ����ð��� timeout ��ŭ�� �ð��� ���ؼ� timeout�� ���� �Ŀ��� �޽����� ���� �� �ְ� �Ѵ�.
            msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));

            result.put("Result", "Ok");
            result.put("MessageID", msg.getMsgId());
            result.put("Message", msg.getMsgBody());

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
                // ���� �޽����� �� �� ���� �����̰� &&
                // �޽��� ���̵� �Էµ� ���̵�� ������ ť���� �ش� �޽����� �����Ѵ�
                if (!msg.isVisibleAt(nowTime) && msg.getMsgId().equals(msgId)) {
                    it.remove();
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
                    // ���� �޽����� �� �� ���� �����̰� &&
                    // �޽��� ���̵� �Էµ� ���̵�� ������ ť���� �ش� �޽����� �����Ѵ�.
                    if (!msg.isVisibleAt(nowTime) && msg.getMsgId().equals(msgId)) {
                        msg.setVisibleFrom(nowTime);
                        result.put("Result", "Ok");
                        break;
                    }
                }

            }

            Gson gson = new GsonBuilder().serializeNulls().create();
            return gson.toJson(result);
    }

    /**
     * �޽��� �ڵ鸵�� ���� �� ������ �޽����� DLQ�� �̵���Ű��
     */
    public void checkMessagesToDLQ() {

        // epoc_millis ����ð�
        long nowTime = now();

        // stream�� ����ؼ� ���� Ƚ���� maxFailCount���� ū �޽����� ã�Ƽ� ó���ϱ�
        queues.stream().filter(m -> m.getFailures() >= maxFailCount).forEach(
                m -> {
                    // �޽����� ���� Ƚ���� �ִ� ���� Ƚ������ ũ�� �޽����� �����Ѵ�.
                    queues.remove(m);
                    // DLQ�� �޽����� �߰��Ѵ�.
                    MyQueueManager.getInstance().getQueueService(name+"-DLQ").addMessage(m);
                }
        );

        // stream�� ����ؼ� process timeout�� ����� �޽����� ã�Ƽ� ó���ϱ�
        queues.stream().filter(m -> m.isProcessTimeout(nowTime)).forEach(
                m -> {
                    // �޽����� �õ� Ƚ���� ���� Ƚ������ ������
                    if (m.getAttempts() < maxFailCount) {
                        // �޽����� �õ� Ƚ���� ������Ų��
                        m.incrementAttempts();
                        // ecpoch_millis ����ð��� timeout ��ŭ�� �ð��� ���ؼ� timeout�� ���� �Ŀ��� �޽����� ���� �� �ְ� �Ѵ�.
                        m.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));
                    } else {
                        // �޽����� �õ� Ƚ���� ���� Ƚ������ ũ��
                        // �޽����� �����Ѵ�.
                        queues.remove(m);
                    }
                }
        );

        // stream�� ����ؼ� ���� Visible ���� ���� �޽��� �߿� �õ� Ƚ���� ���� Ƚ������ ���� �޽����� ã�Ƽ� ó���ϱ�
        queues.stream().filter(m -> !m.isVisibleAt(nowTime)).forEach(
                m -> {
                    // �޽����� �õ� Ƚ���� ���� Ƚ������ ������
                    if (m.getAttempts() < maxFailCount) {
                        // �޽����� �õ� Ƚ���� ������Ų��
                        m.incrementAttempts();
                        // ecpoch_millis ����ð��� timeout ��ŭ�� �ð��� ���ؼ� timeout�� ���� �Ŀ��� �޽����� ���� �� �ְ� �Ѵ�.
                        m.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));
                    } else {
                        // �޽����� �õ� Ƚ���� ���� Ƚ������ ũ��
                        // �޽����� �����Ѵ�
                        queues.remove(m);
                    }
                }
        );

    }

    // epoch_mills ���� �ð�
    long now() {
        return System.currentTimeMillis();
    }
}