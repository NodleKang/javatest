package test.http.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import test.http.handler.PathHandler;

public class PathServlet  extends HttpServlet {

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

    /**
     * GET 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        System.out.println("uri = " + uri);
        if (uri.startsWith("/path")) {
            PathHandler handler = new PathHandler(req, resp);
            handler.run();
//            resp.setStatus(200);
//            resp.setHeader("Content-Type", "application/json");
//            resp.getWriter().write("{\"result\":\"OK\"}");
        }
    }

    /**
     * POST 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        System.out.println("uri = " + uri);
        if (uri.startsWith("/path")) {
            PathHandler handler = new PathHandler(req, resp);
            handler.run();
        }
    }

    /**
     * PUT 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        System.out.println("uri = " + uri);
        if (uri.startsWith("/path")) {
            PathHandler handler = new PathHandler(req, resp);
            handler.run();
        }
    }

    /**
     * DELETE 요청 처리
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        System.out.println("uri = " + uri);
        if (uri.startsWith("/path")) {
            PathHandler handler = new PathHandler(req, resp);
            handler.run();
        }
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
     * 객체를 JSON으로 변환해서 응답 본문에 씁니다. (객체를 JSON으로 변환하는 데 실패하면 IOException이 발생)
     * @param resp
     * @param obj
     * @throws IOException
     */
    private void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(obj));
    }

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
     * 응답 본문을 문자열로 읽어서 반환
     * @param res
     * @return
     * @throws IOException
     */
    private String getBody(HttpServletResponse res) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = res.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String requestBody = buffer.toString();
        return requestBody;
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
}
