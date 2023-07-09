public interface QueueService {

    /**
     * 큐에 메시지 밀어넣기
     * @param queueName
     * @param msgBody
     */
    public void push(String queueName, String msgBody);

    /**
     * 큐에서 메시지 하나 꺼내오기
     * @param queueName
     * @return
     */
    public Message pull(String queueName);

    /**
     * pull()로 받은 메시지 하나를 큐에서 지우기
     * @param queueName
     * @param msgId
     */
    public void delete(String queueName, String msgId);
}
