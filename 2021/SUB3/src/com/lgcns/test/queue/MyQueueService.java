package com.lgcns.test.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MyQueueService {

    private ConcurrentLinkedQueue<Message> queues;

    private final long visibilityTimeout = 300; // 볼 수 있는 시간제한(300초)

    private int size;

    public MyQueueService(String queueName, int size) {
        this.queues = new ConcurrentLinkedQueue<>();
        this.size = size;
    }

    /**
     * 큐에 담겨있는 메시지 사이즈 반환하기
     */
    public int getSize() {
        return queues.size();
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
        queues.add(new Message(msgId, msgBody));
        return "Ok";
    }

    /**
     * 큐에서 메시지 하나 꺼내오기
     */
    public String popMessage() {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (queues.size() == 0) {
            result.put("Result", "No Message");
        }

        // epoc_millis 현재시각
        long nowTime = now();

        // stream을 사용해서 현재 Visible 한 메시지 중에 첫 번째 요소 가져오기
        // optional을 사용해서 null pointer exception에 대비하기
        Optional<Message> msgOpt = queues.stream().filter(m -> m.isVisibleAt(nowTime)).findFirst();
        if (msgOpt.isEmpty()) {
            result.put("Result", "No Message");
        } else {
            // 메시지를 찾으면
            Message msg = msgOpt.get();
            // 메시지에 대한 시도 횟수를 증가시킨다
            msg.incrementAttempts();
            // ecpoch_millis 현재시각에 timeout 만큼의 시간을 더해서 timeout이 지난 후에야 메시지를 꺼낼 수 있게 한다.
            msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(visibilityTimeout));

            result.put("Result", "Ok");
            result.put("MessageID", msg.getMsgId());
            result.put("Message", msg.getMsgBody());

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
                // 현재 메시지가 볼 수 없는 상태이고 &&
                // 메시지 아이디가 입력된 아이디와 같으면 큐에서 해당 메시지를 삭제한다
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
                    // 현재 메시지가 볼 수 없는 상태이고 &&
                    // 메시지 아이디가 입력된 아이디와 같으면 큐에서 해당 메시지를 복원한다.
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

    // epoch_mills 현재 시각
    long now() {
        return System.currentTimeMillis();
    }
}