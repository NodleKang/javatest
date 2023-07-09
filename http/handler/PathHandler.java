package test.http.handler;

import org.eclipse.jetty.util.IO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class PathHandler implements Runnable {

        private HttpServletRequest req;
        private HttpServletResponse resp;

        /**
         * PathHandler 생성자
         * @param req
         * @param resp
         */
        public PathHandler(HttpServletRequest req, HttpServletResponse resp) {
                this.req = req;
                this.resp = resp;
        }

        /**
         * 요청 처리
         * @param in
         * @throws IOException
         */
        private void process(Reader in) throws IOException {
                try {
                        Gson gson = new Gson();
                        JsonObject requestBody = gson.fromJson(in, JsonObject.class);

                        // 요청 헤더 출력
                        System.out.println("Request Headers:");
                        Enumeration<String> headerNames = req.getHeaderNames();
                        while (headerNames.hasMoreElements()) {
                                String headerName = headerNames.nextElement();
                                String headerValue = req.getHeader(headerName);
                                System.out.println(headerName + ": " + headerValue);
                        }
                        System.out.println();

                        // 요청 본문을 JsonObject로 받았으므로 원하는 작업을 수행할 수 있습니다.
                        // 예를 들어, JsonObject에서 필요한 데이터를 추출하거나 조작할 수 있습니다.

                        // 처리 결과를 JsonObject 형식으로 생성
                        JsonObject result = new JsonObject();
                        result.addProperty("result", "OK");

                        // 응답 설정
                        resp.setHeader("Content-Type", "application/json");
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(gson.toJson(result));

                } catch (JsonSyntaxException e) {
                        // JSON 파싱 오류가 발생한 경우
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.setHeader("Content-Type", "application/json");
                        resp.getWriter().write("{\"result\":\"ERROR\", \"message\":\"Invalid JSON format\"}");
                } catch (Throwable th) {
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        resp.setHeader("Content-Type", "application/json");
                        resp.getWriter().write("{\"result\":\"ERROR\"}");
                } finally {
                        IO.close(in);
                }
        }

        /**
         * Runnable 인터페이스의 run 메소드 구현
         */
        @Override
        public void run() {
                try {
                        process(req.getReader());
                } catch (IOException e) {
                        resp.setStatus(HttpServletResponse.SC_OK);
                }
        }
}
