package com.lgcns.test.queue;

import java.time.LocalDateTime;

/**
 * 메시지 클래스
 */
public class Message {

    // 메시지 처리가 실패한 횟수
    private int failures = 0;

    // 메시지 가시성 여부
    private boolean isVisible = true;

    // 메시지가 타임아웃이 경과되기 시작할 시각(epoch_millis)
    private long processTimeoutFrom = 0L;

    private LocalDateTime processTimeoutFromDate = null;

    // 메시지 아이디
    private String msgId;

    // 메시지 본문
    private String msgBody;

    /**
     * 메시지 생성자
     * @param msgId 메시지 아이디 문자열
     * @param msgBody 메시지 본문 문자열
     */
    Message(String msgId, String msgBody) {
        this.msgId = msgId;
        this.msgBody = msgBody;
    }

    /**
     * 메시지가 Timeout이 될 시각(epoch_millis)을 설정합니다.
     * 즉, pointInTime 부터는 ProcessTimeout이 지난 것입니다.
     */
    protected void setProcessTimeoutFrom(long pointInTime) {
        this.processTimeoutFrom = pointInTime;
        this.processTimeoutFromDate = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(pointInTime), java.time.ZoneId.systemDefault());
    }

    /**
     * 메시지가 pointInTime(epoch_millis) 시각에 Process Timeout이 되었는지를 반환합니다.
     * processTimeoutFrom이 0이면 false를 반환합니다.
     */
    public boolean isProcessTimeout(long pointInTime) {
        if (processTimeoutFrom == 0L) {
            return false;
        }
        return processTimeoutFrom - 100 < pointInTime;
    }

    /**
     * 메시지의 가시성 여부를 설정합니다.
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * 메시지의 가시성 여부를 반환합니다.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 메시지가 실패한 횟수를 반환합니다.
     */
    protected int getFailures() {
        return this.failures;
    }

    /**
     * 메시지가 실패한 횟수를 초기화합니다.
     */
    protected void resetFailures() {
        this.failures = 0;
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

    public String getMsgBody() {
        return this.msgBody;
    }

}
