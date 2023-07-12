package com.lgcns.test.queue;

/**
 * 메시지 클래스
 */
public class Message {

    // 메시지가 전달된 횟수
    private int attempts;

    // 메시지 처리가 실패한 횟수
    private int failures;

    // 메시지 가시성을 허용하기 시작할 시각(epoch_millis)
    private long visibleFrom;

    // 메시지가 타임아웃이 경과되기 시작할 시각(epoch_millis)
    private long processTimeoutFrom;

    // 메시지 아이디
    private String msgId;

    // 메시지 본문
    private String msgBody;

    /**
     * 메시지 생성자
     * @param msgBody 메시지 본문 문자열
     */
    Message(String msgBody) {
        this.msgBody = msgBody;
    }

    /**
     * 메시지 생성자
     * @param msgId 메시지 아이디 문자열
     * @param msgBody 메시지 본문 문자열
     */
    Message(String msgId, String msgBody) {
        this.attempts = 0;
        this.failures = 0;
        this.visibleFrom = System.currentTimeMillis();
        this.processTimeoutFrom = 0;
        this.msgId = msgId;
        this.msgBody = msgBody;
    }

    /**
     * 메시지를 볼 수 있게 하는 시각 pointInTime(epoch_millis)을 설정합니다.
     * @param pointInTime
     */
    protected void setVisibleFrom(long pointInTime) {
        this.visibleFrom = pointInTime;
    }

    /**
     * 메시지가 pointInTime(epoch_millis) 시각에 볼 수 있는지를 반환합니다.
     * @param pointInTime
     * @return true 볼 수 있음, false 볼 수 없음
     */
    public boolean isVisibleAt(long pointInTime) {
        return visibleFrom < pointInTime;
    }

    /**
     * 메시지가 pointInTime(epoch_millis) 시각에 Process Timeout이 되었는지를 반환합니다.
     * @param pointInTime
     * @return true Process Timeout이 되었음, false Process Timeout이 되지 않음
     */
    public boolean isProcessTimeout(long pointInTime) {
        return processTimeoutFrom < pointInTime;
    }

    /**
     * 메시지가 전달된 횟수를 반환합니다.
     * @return 메시지가 전달된 횟수
     */
    protected int getAttempts() {
        return this.attempts;
    }

    /**
     * 메시지 전달된 횟수를 증가시킵니다.
     */
    protected void incrementAttempts() {
        this.attempts++;
    }

    /**
     * 메시지 처리가 실패한 횟수를 반환합니다.
     * @return 메시지 처리가 실패한 횟수
     */
    protected int getFailures() {
        return this.failures;
    }

    /**
     * 메시지 처리가 실패한 횟수를 증가시킵니다.
     */
    protected void incrementFailures() {
        this.failures++;
    }

    public String getMsgId() {
        return this.msgId;
    }

    protected void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgBody() {
        return this.msgBody;
    }

}
