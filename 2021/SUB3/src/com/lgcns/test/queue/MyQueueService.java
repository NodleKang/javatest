package com.lgcns.test.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MyQueueService {

    private ConcurrentLinkedQueue<Message> queues;

    private final long visibilityTimeout = 300; // �� �� �ִ� �ð�����(300��)

    private int size;

    public MyQueueService(String queueName, int size) {
        this.queues = new ConcurrentLinkedQueue<>();
        this.size = size;
    }

    /**
     * ť�� ����ִ� �޽��� ������ ��ȯ�ϱ�
     */
    public int getSize() {
        return queues.size();
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
        queues.add(new Message(msgId, msgBody));
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
            msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(visibilityTimeout));

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

    // epoch_mills ���� �ð�
    long now() {
        return System.currentTimeMillis();
    }
}