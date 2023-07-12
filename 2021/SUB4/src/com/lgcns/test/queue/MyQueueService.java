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

    public MyQueueService(String queueName, int size, int processTimeout, int maxFailCount) {
        this.name = queueName;
        this.queues = new ConcurrentLinkedQueue<>();
        this.size = size;
        this.processTimeout = processTimeout;
        this.maxFailCount = maxFailCount;
    }

    /**
     * 큐에 담겨있는 메시지 사이즈 반환하기
     */
    public int getSize() {
        return queues.size();
    }

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
            msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));

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

    /**
     * 메시지 핸들링에 여러 번 실패한 메시지를 DLQ로 이동시키기
     */
    public void checkMessagesToDLQ() {

        // epoc_millis 현재시각
        long nowTime = now();

        // stream을 사용해서 실패 횟수가 maxFailCount보다 큰 메시지를 찾아서 처리하기
        queues.stream().filter(m -> m.getFailures() >= maxFailCount).forEach(
                m -> {
                    // 메시지의 실패 횟수가 최대 실패 횟수보다 크면 메시지를 삭제한다.
                    queues.remove(m);
                    // DLQ에 메시지를 추가한다.
                    MyQueueManager.getInstance().getQueueService(name+"-DLQ").addMessage(m);
                }
        );

        // stream을 사용해서 process timeout이 경과된 메시지를 찾아서 처리하기
        queues.stream().filter(m -> m.isProcessTimeout(nowTime)).forEach(
                m -> {
                    // 메시지의 시도 횟수가 실패 횟수보다 작으면
                    if (m.getAttempts() < maxFailCount) {
                        // 메시지의 시도 횟수를 증가시킨다
                        m.incrementAttempts();
                        // ecpoch_millis 현재시각에 timeout 만큼의 시간을 더해서 timeout이 지난 후에야 메시지를 꺼낼 수 있게 한다.
                        m.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));
                    } else {
                        // 메시지의 시도 횟수가 실패 횟수보다 크면
                        // 메시지를 삭제한다.
                        queues.remove(m);
                    }
                }
        );

        // stream을 사용해서 현재 Visible 하지 않은 메시지 중에 시도 횟수가 실패 횟수보다 작은 메시지를 찾아서 처리하기
        queues.stream().filter(m -> !m.isVisibleAt(nowTime)).forEach(
                m -> {
                    // 메시지의 시도 횟수가 실패 횟수보다 작으면
                    if (m.getAttempts() < maxFailCount) {
                        // 메시지의 시도 횟수를 증가시킨다
                        m.incrementAttempts();
                        // ecpoch_millis 현재시각에 timeout 만큼의 시간을 더해서 timeout이 지난 후에야 메시지를 꺼낼 수 있게 한다.
                        m.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(processTimeout));
                    } else {
                        // 메시지의 시도 횟수가 실패 횟수보다 크면
                        // 메시지를 삭제한다
                        queues.remove(m);
                    }
                }
        );

    }

    // epoch_mills 현재 시각
    long now() {
        return System.currentTimeMillis();
    }
}