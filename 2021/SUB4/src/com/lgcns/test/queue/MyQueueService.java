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

    private long processTimeout = 300; // 볼 수 있는 시간제한(300초)

    private int maxFailCount = 3; // 실패 횟수

    private int waitTime = 10; // 10초

    private boolean isDLQ = false; // DLQ 여부

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
     * 큐에 담겨있는 메시지 사이즈 반환하기
     */
    public int getQueueSize() {
        return queues.size();
    }

    public int getWaitTime() {
        return waitTime;
    }

    /**
     * 큐에 메시지 추가하기
     */
    public String addMessage(Message message) {
        if (queues.size() >= size) {
            // 큐의 크기가 최대 크기보다 크면 메시지를 넣지 않고 종료
            return "Queue Full";
        }
        // 큐에 새 메시지 추가
        queues.add(message);
        return "Ok";
    }

    /**
     * 큐에 메시지 추가하기
     */
    public String addMessage(String msgBody) {
        if (queues.size() >= size) {
            // 큐의 크기가 최대 크기보다 크면 메시지를 넣지 않고 종료
            return "Queue Full";
        }
        // 큐에 새 메시지 추가
        String msgId = UUID.randomUUID().toString();
        Message msg = new Message(msgId, msgBody);
        queues.add(msg);
        System.out.println("[addMessage]" + System.currentTimeMillis() + " : " + name + " : " + msgBody + " : " + msgId);
        return "Ok";
    }

    /**
     * 일반 큐의 메시지 가져오기
     */
    public String popMessage() {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues.size() == 0) {
            result.put("Result", "No Message");
        }

        // epoc_millis 현재시각
        long nowTime = now();

        // 현재 볼 수 있는 메시지이고,
        // ProcessTimeout 시간이 지나지 않았고,
        // 실패 횟수가 최대 실패 횟수보다 작은 메시지 중에 첫번쨰 메시지 찾기
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
     * 큐에서 메시지 삭제
     * @param msgId
     * @return
     */
    public String removeMessage(String msgId) {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues != null) {

            // epoch_millis 현재시각
            long nowTime = now();

            // 큐에 루프를 돌면서
            for (Iterator<Message> it = queues.iterator(); it.hasNext();) {
                // 메시지를 꺼내온다
                Message msg = it.next();
                // 메시지 아이디가 입력된 아이디와 같으면 큐에서 해당 메시지를 삭제한다
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
     * 큐에 메시지 복원
     * @param msgId
     * @return
     */
    public String restoreMessage(String msgId) {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues != null) {

            // epoch_millis 현재시각
            long nowTime = now();

            // 큐에 루프를 돌면서
            for (Iterator<Message> it = queues.iterator(); it.hasNext();) {
                // 메시지를 꺼내온다
                Message msg = it.next();
                // 실패 횟수가 최대 실패 횟수보다 작고 &&
                // 메시지 아이디가 입력된 아이디와 같으면 큐에서 해당 메시지를 복원한다.
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
     * ProcessTimeout이 지난 메시지를 처리하기
     */
    public void processTimeoutMessages() {

        // epoc_millis 현재시각
        long nowTime = now();

        // 현재 processTimeout이 지난 메시지 처리하기
        for (Message m: queues) {
            if (m.isProcessTimeout(nowTime)) {
                // 메시지 복원하기 (메소드 안에서 실패횟수를 증가시키고, processTimeout을 0으로 만든다)
                restoreMessage(m.getMsgId());
            }
        }
    }

    /**
     * 실패 횟수가 최대 실패 횟수를 넘은 메시지 처리하기
     */
    public void processOverFailedMessages() {

        // 현재 실패 횟수가 최대 실패 횟수를 넘은 메시지 처리하기
        for (Message m : queues) {
            if (m.getFailures() > maxFailCount) {
                // 메시지를 DLQ로 이동시키기
                moveMessageToDLQ(m);
            }
        }

    }

    /**
     * 메시지를 DLQ로 이동시키기
     */
    private void moveMessageToDLQ(Message m) {
        System.out.println("[moveMessageToDLQ] before move " + System.currentTimeMillis() + " : " + name + " : " + getQueueSize());
        // 기본 큐에서 메시지 삭제
        queues.remove(m);
        // DLQ용 큐에 메시지 추가
        MyQueueManager.getInstance().getQueueService(name+"-DLQ").addMessage(m);
        System.out.println("[moveMessageToDLQ] after move " + System.currentTimeMillis() + " : " + name + " : " + getQueueSize());
    }

    // epoch_mills 현재 시각
    long now() {
        return System.currentTimeMillis();
    }
}