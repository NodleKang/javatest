import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestHandler extends AbstractHandler {
    private final HttpRequestWorker httpRequestWorker;

    public RequestHandler(HttpRequestWorker httpRequestWorker) {
        this.httpRequestWorker = httpRequestWorker;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (request.getMethod().equals("POST")) {
            String requestBody = extractRequestBody(request);
            httpRequestWorker.appendRequestBody(requestBody);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }

    private String extractRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }
}
