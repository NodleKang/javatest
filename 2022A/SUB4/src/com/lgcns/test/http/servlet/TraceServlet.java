package com.lgcns.test.http.servlet;

import com.lgcns.test.analyzer.HistoryAnalyzer;
import com.lgcns.test.analyzer.ServiceNode;
import com.lgcns.test.util.MultipleProcessFileWriter;
import com.lgcns.test.util.MyFile;
import com.lgcns.test.util.MyJson;
import com.lgcns.test.util.MyString;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class TraceServlet extends HttpServlet {

    private int port;

    public TraceServlet(int port) {
        this.port = port;
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
    }

    /**
     * 서블릿 종료 시 필요한 작업 처리, 서블릿이 종료될 때 한 번 실행됩니다.
     */
    @Override
    public void destroy() {
        // 서블릿이 종료될 때 필요한 작업이 있으면 여기에 추가합니다.
        // 이 메소드는 서블릿이 종료될 때 한 번 실행됩니다.
        // 서블릿이 실행되는 동안 필요한 작업이 없으면 이 메소드를 비워둡니다.
        super.destroy();
    }

    private String getRequestHistory(String xRequesstId) {
        // proxy 로그 파일 읽기
        List<String> lines = MyFile.reverseReadFileToList("proxy.txt", "UTF-8");
        // x-request-id 에 해당하는 로그만 추출하여 트리 구조로 변환
        ServiceNode serviceNode = HistoryAnalyzer.populateTree(xRequesstId, lines);
        serviceNode.getParentName();
        return serviceNode.toString();
    }


    /**
     * http Method GET 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html; charset=utf-8");
        String xRequestId = MyString.splitToStringArray(req.getRequestURI(), "/")[2];
        MyJson.writeJsonToHttpResponse(resp, getRequestHistory(xRequestId));
    }

}
