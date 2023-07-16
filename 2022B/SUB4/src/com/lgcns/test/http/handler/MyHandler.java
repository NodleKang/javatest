package com.lgcns.test.http.handler;

import com.lgcns.test.util.MyHttp;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            // POST 요청 처리
            if ("/path1".equalsIgnoreCase(req.getPathInfo())) {
                // path1에 대한 처리
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("Handling POST request for path1");
                baseRequest.setHandled(true);
            } else if ("/path2".equalsIgnoreCase(req.getPathInfo())) {
                // path2에 대한 처리
            } else if ("/path3".equalsIgnoreCase(req.getPathInfo())) {
                // path3에 대한 처리
            }
        } else if ("GET".equalsIgnoreCase(req.getMethod())) {
            // GET 요청 처리
            if ("/queueInfo".equalsIgnoreCase(req.getPathInfo())) {
                // queueInfo에 대한 처리
                handleQueueInfo(req, resp);
                baseRequest.setHandled(true);
            } else if ("/path5".equalsIgnoreCase(req.getPathInfo())) {
                // path5에 대한 처리
            }
        }
    }

    private void handleQueueInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QueueInfo queueInfo = (QueueInfo) MyHttp.readRequestBodyAsJsonObject(req, QueueInfo.class);
        for (String inputQueueURI: queueInfo.getInputQueueURIs()) {

        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println("Handling POST request for queueInfo");

    }

}
