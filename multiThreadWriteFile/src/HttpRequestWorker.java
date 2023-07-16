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
        // HttpRequestWorker�� ������ Jetty �������� ó���ǹǷ� run() �޼���� ����Ӵϴ�.
        // Jetty ������ RequestHandler Ŭ�������� ó���մϴ�.
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
