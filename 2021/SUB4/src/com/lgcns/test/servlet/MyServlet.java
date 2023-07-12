package com.lgcns.test.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lgcns.test.handler.QueueConfig;
import com.lgcns.test.queue.MyQueueManager;
import com.lgcns.test.queue.MyQueueService;
import com.lgcns.test.util.MyJson;
import com.lgcns.test.util.MyString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyServlet  extends HttpServlet {

    /**
     * POST 요청 처리
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String queueName = "";
        String msgId = "";

        // 요청 URI에서 큐 이름 추출
        String uri = req.getRequestURI();
        String[] uris = MyString.splitToStringArray(uri, "/");
        if (uris.length < 3) {
            return;
        }
        queueName = uris[2];

        // 요청 URI에서 메시지 ID 추출
        if (uris.length == 4) {
            msgId = uris[3];
        }

        if (uri.startsWith("/CREATE")) {
            // Queue 생성 요청 처리
            handleCreate(queueName, req, resp);
        } else if (uri.startsWith("/SEND")) {
            // 메시지 송신 요청 처리
            handleSend(queueName, req, resp);
        } else if (uri.startsWith("/ACK")) {
            // 메시지 핸들링 완료 처리
            handleAck(queueName, msgId, req, resp);
        } else if (uri.startsWith("/FAIL")) {
            // 메시지 핸들링 실패 처리
            handleFail(queueName, msgId, req, resp);
        }
    }

    /**
     * GET 요청 처리
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String queueName = "";
        String msgId = "";

        // 요청 URI에서 큐 이름 추출
        String uri = req.getRequestURI();
        String[] uris = MyString.splitToStringArray(uri, "/");
        if (uris.length < 3) {
            return;
        }
        queueName = uris[2];

        // 요청 URI에서 메시지 ID 추출
        if (uris.length == 4) {
            msgId = uris[3];
        }

        if (uri.startsWith("/RECEIVE")) {
            handleReceive(queueName, req, resp);
        } else if (uri.startsWith("/DLQ")) {
            handleDLQ(queueName, req, resp);
        }
    }

    /**
     * CREATE 요청 처리
     */
    private void handleCreate(String queueName, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 요청 본문에서 큐 사이즈 추출
        String body = readBodyAsString(req);
        Gson gson = new GsonBuilder().serializeNulls().create();
        QueueConfig conf = gson.fromJson(body, QueueConfig.class);
        JsonObject jo = MyJson.convertBufferedReaderToJsonObject(req.getReader());

        if ( MyQueueManager.getInstance().getQueueService(queueName) != null) {
            // 큐가 이미 존재하면 Queue Exist 응답
            resp.setStatus(200);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write("{\"Result\":\"Queue Exist\"}");
            return;
        } else {
            // 큐가 존재하지 않으면 큐(일반 메시지용, DLQ용) 생성
            MyQueueManager.getInstance().createQueueService(queueName, conf.getQueueSize(), conf.getWaitTime(), conf.getMaxFailCount());
            MyQueueManager.getInstance().createQueueService(queueName+"-DLQ", conf.getQueueSize(), conf.getWaitTime(), conf.getMaxFailCount());
            resp.setStatus(200);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write("{\"Result\":\"Ok\"}");
            return;
        }

    }

    /**
     * SEND 요청 처리
     */
    public void handleSend(String queueName, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 요청 본문에서 메시지 추출
        JsonObject jo = MyJson.convertBufferedReaderToJsonObject(req.getReader());
        String msgBody = jo.get("Message").getAsString();

        // 큐에 메시지 추가
        MyQueueService queueService = MyQueueManager.getInstance().getQueueService(queueName);
        String result = queueService.addMessage(msgBody);
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write("{\"Result\":\"" + result + "\"}");
        return;

    }

    /**
     * RECEIVE 요청 처리
     */
    public void handleReceive(String queueName, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        // 큐에서 메시지 추출
        MyQueueService queueService = MyQueueManager.getInstance().getQueueService(queueName);
        String result = queueService.popMessage();
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write(result);
        return;
    }

    /**
     * ACK 요청 처리
     */
    public void handleAck(String queueName, String msgId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MyQueueService queueService = MyQueueManager.getInstance().getQueueService(queueName);
        String result = queueService.removeMessage(msgId);
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write(result);
        return;

    }

    /**
     * FAIL 요청 처리
     */
    public void handleFail(String queueName, String msgId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MyQueueService queueService = MyQueueManager.getInstance().getQueueService(queueName);
        String result = queueService.restoreMessage(msgId);
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write(result);
        return;

    }

    /**
     * DLQ 요청 처리
     * @param queueName 원본큐이름과 DLQ 키워드를 붙여서 DLQ 큐 이름을 만들어서 사용
     */
    public void handleDLQ(String queueName, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // DLQ 큐에서 메시지 추출
        MyQueueService queueService = MyQueueManager.getInstance().getQueueService(queueName+"-DLQ");
        String result = queueService.popMessage();
        // 메시지 삭제
        String msgId = MyJson.convertStringToJsonObject(result).get("MessageId").getAsString();
        result = queueService.removeMessage(msgId);
        // 응답
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write(result);
        return;

    }

    /**
     * 요청 본문을 JSON으로 읽어서 객체로 반환(요청 본문이 JSON 형식이 아니거나 요청 본문이 없으면 null을 반환)
     * @param req
     * @return
     */
    private JsonObject readBodyAsJson(HttpServletRequest req) {

        try {
            // HTTP 요청 본문을 읽기 위한 BufferedReader 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            StringBuilder requestBody = new StringBuilder();
            String line;

            // BufferedReader를 사용하여 HTTP 요청 본문을 읽어옴
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            reader.close();

            // GSON을 사용하여 JSON을 객체로 변환
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(requestBody.toString(), JsonObject.class);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 요청 본문을 String 객체로 반환(요청 본문이 JSON 형식이 아니거나 요청 본문이 없으면 null을 반환)
     * @param req
     * @return
     */
    private String readBodyAsString(HttpServletRequest req) {

        try {
            // HTTP 요청 본문을 읽기 위한 BufferedReader 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            StringBuilder requestBody = new StringBuilder();
            String line;

            // BufferedReader를 사용하여 HTTP 요청 본문을 읽어옴
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            reader.close();

            return requestBody.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 객체를 JSON으로 변환해서 응답 본문에 씁니다. (객체를 JSON으로 변환하는 데 실패하면 IOException이 발생)
     * @param resp
     * @param obj
     * @throws IOException
     */
    private void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        Gson gson = new GsonBuilder().serializeNulls().create();
        resp.getWriter().write(gson.toJson(obj));
    }

    /*
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 서블릿 클래스가 특정 요청 방식(GET, POST 등)에만 관심이 있다면
        // doGet(), doPost() 등을 오버라이딩해서 작업을 처리하면 됩니다.
        // 모든 요청 방식에 대해 동일한 작업을 처리하고 싶다면
        // service() 메소드를 오버라이딩해서 작업을 처리하면 됩니다.
        System.out.println("service");
    }
    */

    /**
     * 요청 본문을 문자열로 읽어서 반환
     * @param req
     * @return
     * @throws IOException
     */
    private String getBody(HttpServletRequest req) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String requestBody = buffer.toString();
        return requestBody;
    }

    /**
     * 서블릿 초기화 시 필요한 작업 처리, 서블릿이 최초로 실행될 때 한 번만 실행됩니다.
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        // 서블릿 초기화 시 필요한 작업이 있으면 여기서 처리합니다.
        // 이 메소드는 서블릿이 최초로 실행될 때 한 번만 실행됩니다.
        // 서블릿이 실행되는 동안 필요한 작업이 없으면 이 메소드는 비워둡니다.
        System.out.println("init");
    }

    /**
     * 서블릿 종료 시 필요한 작업 처리, 서블릿이 종료될 때 한 번 실행됩니다.
     */
    @Override
    public void destroy() {
        // 서블릿 종료 시 필요한 작업이 있으면 여기서 처리합니다.
        // 이 메소드는 서블릿이 종료될 때 한 번만 실행됩니다.
        // 서블릿이 실행되는 동안 필요한 작업이 없으면 이 메소드는 비워둡니다.
        System.out.println("destroy");
    }

}
