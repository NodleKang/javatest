import java.io.FileWriter;
import java.io.IOException;

public class HttpRequestWorker implements Runnable {
    private final String mergeFilePath;
    private volatile boolean running;

    public HttpRequestWorker(String mergeFilePath) {
        this.mergeFilePath = mergeFilePath;
        this.running = true;
    }

    @Override
    public void run() {
        // HttpRequestWorker의 로직은 Jetty 서버에서 처리되므로 run() 메서드는 비워둡니다.
        // Jetty 서버의 RequestHandler 클래스에서 처리합니다.
    }

    public void stop() {
        running = false;
    }

    public void appendRequestBody(String requestBody) {
        synchronized (mergeFilePath) {
            try (FileWriter mergeFileWriter = new FileWriter(mergeFilePath, true)) {
                mergeFileWriter.write(requestBody + "\n");
                mergeFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
