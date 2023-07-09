import java.util.*;
import java.util.concurrent.*;

// 메모리 내 큐 서비스
public class MemoryQueueService implements QueueService {

    private final Map<String, Queue<Message>> queues;

    private long visibilityTimeout;

    /**
     * 생성자
     */
    public MemoryQueueService() {

        this.queues = new ConcurrentHashMap<>();

        // Properties 파일 읽기
        PropUtil propUtil = new PropUtil("resources/config.properties");

        // 볼 수 있는 시간제한(초)
        this.visibilityTimeout = Integer.parseInt(propUtil.getConfig().getProperty("visibilityTimeout", "30"));
    }

    /**
     * 큐에 메시지 밀어넣기
     * @param queueName
     * @param msgBody
     */
    @Override
    public void push(String queueName, String msgBody) {
        Queue<Message> queue = queues.get(queueName);
        if (queue == null) {
            // 기존에 없던 큐면 새 큐를 생성해서 queues에 추가
            queue = new ConcurrentLinkedDeque<>();
            queues.put(queueName, queue);
        }
        // 큐에 새 메시지 추가
        queue.add(new Message(msgBody));
    }

    /**
     * 큐에서 메시지 하나 꺼내오기
     * @param queueName
     * @return
     */
    @Override
    public Message pull(String queueName) {
        Queue<Message> queue = queues.get(queueName);
        if (queue == null) {
            // 큐가 없으면 null 반환
            return null;
        }

        // epoc_millis 현재시각
        long nowTime = now();

        // stream을 사용해서 현재 Visible 한 메시지 중에 첫 번째 요소 가져오기
        // optional을 사용해서 null pointer exception에 대비하기
        Optional<Message> msgOpt = queue.stream().filter(m -> m.isVisibleAt(nowTime)).findFirst();
        if (msgOpt.isEmpty()) {
            return null;
        } else {
            // 메시지를 찾으면
            Message msg = msgOpt.get();
            // ID 설정한다
            msg.setMsgId(UUID.randomUUID().toString());
            // 메시지에 대한 시도 횟수를 증가시킨다
            msg.incrementAttempts();
            // ecpoch_millis 현재시각에 timeout 만큼의 시간을 더해서 timeout이 지난 후에야 메시지를 꺼낼 수 있게 한다.
            msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(visibilityTimeout));

            return new Message(msg.getMsgBody(), msg.getMsgId());
        }
    }

    /**
     * pull()로 받은 메시지 하나를 큐에서 지우기
     * @param queueName
     * @param msgId
     */
    @Override
    public void delete(String queueName, String msgId) {
        Queue<Message> queue = queues.get(queueName);
        if (queue != null) {

            // epoch_millis 현재시각
            long nowTime = now();

            // 큐에 루프를 돌면서
            for (Iterator<Message> it = queue.iterator(); it.hasNext();) {
                // 메시지를 꺼내온다
                Message msg = it.next();
                // 현재 메시지가 볼 수 없는 상태이고 &&
                // 메시지 아이디가 입력된 아이디와 같으면 큐에서 해당 메시지를 삭제한다
                if (!msg.isVisibleAt(nowTime) && msg.getMsgId().equals(msgId)) {
                    it.remove();
                    break;
                }
            }

        }
    }

    // epoch_mills 현재 시각
    long now() {
        return System.currentTimeMillis();
    }
}
