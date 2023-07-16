package com.lgcns.test.http.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ProxyServlet extends HttpServlet {

    private String targetPath = "";

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
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


    /**
     * http Method GET 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp);
    }

    /**
     * http Method POST 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp);
    }

    /**
     * http Method PUT 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp);
    }

    /**
     * http Method DELETE 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp);
    }


    /**
     * http Method 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 요청된 HTTP Method, Path, Query를 그대로 전달하기 위해 service() 메소드를 오버라이드합니다.
        // super.service()를 호출하지 않고 proxyRequest() 메소드만 호출합니다.
        proxyRequest(req, resp);
    }

    /**
     * 원본 요청을 Proxy 요청으로 전달합니다.
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void proxyRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 원본 요청을 전달할 대상 URL 설정
        String targetUrl = createTargetUrl(req);
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // 원본 http Method 복사
        String requestMethod = req.getMethod();
        con.setRequestMethod(requestMethod);

        // 원본 요청 헤더를 프록시 요청에 복사
        copyRequestHeaders(req, con);

        // 원본 요청 파라미터를 프록시 요청에 복사
        copyRequestParameters(req, con);

        // 원본 요청 바디를 프록시 요청에 복사
        copyRequestBody(req, con);

        // 프록시 요청을 보내고 응답을 받습니다.
        int responseCode = con.getResponseCode();

        // 프록시 응답 헤더를 원본 응답에 복사
        copyResponseHeaders(con, resp);

        // 프록시 응답 파라미터를 원본 응답에 복사
        copyResponseParameters(con, resp);

        // 프록시 응답 바디를 원본 응답에 복사
        copyResponseBody(con, resp);

        // 프록시 응답 코드를 원본 응답에 복사
        resp.setStatus(responseCode);

        // 프록시 응답을 보냈으면 연결을 닫습니다.
        con.disconnect();
    }

    /**
     * 원본 요청에서 prefix로 시작하는 모든 헤더의 이름과 값을 반환합니다.
     * @param req
     * @param namePrefix
     * @return
     */
    private Map<String, List<String>> getHeaders(HttpServletRequest req, String namePrefix) {
        Map<String, List<String>> headers = new HashMap<>();
        // 요청에서 모든 헤더의 이름을 가져와서 순회합니다.
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // 헤더의 이름이 prefix로 시작하는 경우에만 처리 진행
            if (headerName.toLowerCase().startsWith(namePrefix)) {
                // 같은 이름의 헤더가 여러 개일 수 있으므로 리스트에 값을 담습니다.
                List<String> headerValues = new ArrayList<>();
                Enumeration<String> headerValuesEnum = req.getHeaders(headerName);
                while (headerValuesEnum.hasMoreElements()) {
                    headerValues.add(headerValuesEnum.nextElement());
                }
                headers.put(headerName, headerValues);
            }
        }
        return headers;
    }

    /**
     * 원본 요청에서 이름이 headerName과 같은 헤더의 이름과 값을 반환합니다.
     * @param req
     * @param headerName
     * @return
     */
    private String getHeaderValue(HttpServletRequest req, String headerName) {
        String headerValue = req.getHeader(headerName);
        if (headerValue != null && !headerValue.isEmpty()) {
            String[] values = headerValue.split(",");
            return values[0].trim();
        }
        return null;
    }

    /**
     * 대상 경로와 요청 URI를 결합하여 대상 URL을 생성합니다. Request에 쿼리 문자열이 포함된 경우도 대응됨
     * @param req
     * @return
     */
    private String createTargetUrl(HttpServletRequest req) {
        String targetUrl = targetPath + req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString != null) {
            targetUrl += "?" + queryString;
        }
        return targetUrl;
    }

    /**
     * 원본 요청 헤더를 프록시 요청에 복사합니다.
     * @param req
     * @param con
     * @throws IOException
     */
    private void copyRequestHeaders(HttpServletRequest req, HttpURLConnection con) throws IOException {
//        req.getHeaderNames().asIterator().forEachRemaining(headerName -> {
//            String headerValue = req.getHeader(headerName);
//            con.setRequestProperty(headerName, headerValue);
//        });
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            con.addRequestProperty(headerName, headerValue);
        }
    }

    /**
     * 원본 요청 파라미터를 프록시 요청에 복사합니다.
     * @param req
     * @param con
     * @throws IOException
     */
    private void copyRequestParameters(HttpServletRequest req, HttpURLConnection con) throws IOException {
//        req.getParameterMap().forEach((paramName, paramValues) -> {
//            for (String paramValue : paramValues) {
//                con.addRequestProperty(paramName, paramValue);
//            }
//        });
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            for (String paramValue : paramValues) {
                con.addRequestProperty(paramName, paramValue);
            }
        }
    }

    /**
     * 원본 요청 바디를 프록시 요청에 복사합니다.
     * @param req
     * @param con
     * @throws IOException
     */
    private void copyRequestBody(HttpServletRequest req, HttpURLConnection con) throws IOException {
        int contentLength = req.getContentLength();
        if (contentLength > 0) {
            con.setDoOutput(true);
            try (OutputStream outputStream = con.getOutputStream();
                 InputStream inputStream = req.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    /**
     * 프록시 응답 헤더를 원본 응답에 복사합니다.
     * @param con
     * @param resp
     * @throws IOException
     */
    private void copyResponseHeaders(HttpURLConnection con, HttpServletResponse resp) throws IOException {
//        con.getHeaderFields().forEach((headerName, headerValues) -> {
//            if (headerName != null) {
//                for (String headerValue : headerValues) {
//                    resp.addHeader(headerName, headerValue);
//                }
//            }
//        });
        Map<String, List<String>> headerFields = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            String headerName = entry.getKey();
            if (headerName != null) {
                List<String> headerValues = entry.getValue();
                for (String headerValue : headerValues) {
                    resp.addHeader(headerName, headerValue);
                }
            }
        }
    }

    /**
     * 프록시 응답 파라미터를 원본 응답에 복사합니다.
     * @param con
     * @param resp
     * @throws IOException
     */
    private void copyResponseParameters(HttpURLConnection con, HttpServletResponse resp) throws IOException {
//        con.getHeaderFields().forEach((headerName, headerValues) -> {
//            if (headerName != null) {
//                for (String headerValue : headerValues) {
//                    resp.addHeader(headerName, headerValue);
//                }
//            }
//        });
        Map<String, List<String>> headerFields = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            String headerName = entry.getKey();
            if (headerName != null) {
                List<String> headerValues = entry.getValue();
                for (String headerValue : headerValues) {
                    resp.addHeader(headerName, headerValue);
                }
            }
        }
    }

    /**
     * 프록시 응답 바디를 원본 응답에 복사합니다.
     * @param con
     * @param resp
     * @throws IOException
     */
    private void copyResponseBody(HttpURLConnection con, HttpServletResponse resp) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(con.getInputStream());
            outputStream = new BufferedOutputStream(resp.getOutputStream());
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

//        try (InputStream inputStream = con.getInputStream();
//             OutputStream outputStream = resp.getOutputStream()) {
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//        }
    }

}
