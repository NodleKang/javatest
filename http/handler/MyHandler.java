package test.http.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyHandler extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            // POST 요청 처리
            if ("/path1".equalsIgnoreCase(request.getPathInfo())) {
                // path1에 대한 처리
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Handling POST request for path1");
                baseRequest.setHandled(true);
            } else if ("/path2".equalsIgnoreCase(request.getPathInfo())) {
                // path2에 대한 처리
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Handling POST request for path2");
                baseRequest.setHandled(true);
            } else if ("/path3".equalsIgnoreCase(request.getPathInfo())) {
                // path3에 대한 처리
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Handling POST request for path3");
                baseRequest.setHandled(true);
            }
        } else if ("GET".equalsIgnoreCase(request.getMethod())) {
            // GET 요청 처리
            if ("/path4".equalsIgnoreCase(request.getPathInfo())) {
                // path4에 대한 처리
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Handling GET request for path4");
                baseRequest.setHandled(true);
            } else if ("/path5".equalsIgnoreCase(request.getPathInfo())) {
                // path5에 대한 처리
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Handling GET request for path5");
                baseRequest.setHandled(true);
            }
        }
    }
}
