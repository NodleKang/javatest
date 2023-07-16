import java.io.*;

public class LogMergeWorker implements Runnable {
    private final String tmpFilePath;
    private final String mergeFilePath;
    private volatile boolean running;

    public LogMergeWorker(String tmpFilePath, String mergeFilePath) {
        this.tmpFilePath = tmpFilePath;
        this.mergeFilePath = mergeFilePath;
        this.running = true;
    }

    @Override
    public void run() {
        try (FileInputStream tmpFileStream = new FileInputStream(tmpFilePath);
             BufferedReader tmpReader = new BufferedReader(new InputStreamReader(tmpFileStream));
             FileWriter mergeFileWriter = new FileWriter(mergeFilePath, true)) {
            while (running) {
                String line;
                while ((line = tmpReader.readLine()) != null) {
                    synchronized (mergeFileWriter) {
                        mergeFileWriter.write(line + "\n");
                        mergeFileWriter.flush();
                    }
                }
                Thread.sleep(1000); // 1초마다 tmp.log를 체크
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }
}
