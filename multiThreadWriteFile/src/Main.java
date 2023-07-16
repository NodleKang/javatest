import org.eclipse.jetty.server.Server;
/*
���� �Ʒ� ������ �����ϴ� java�� ���α׷��� ������ �Ѵ�. �ڵ带 �ۼ��ش޶�.
1. tmp.log ���Ͽ� ���ο� ������ �߰��� ������ �� ������ �о merge.log ���Ͽ� �����̴� �۾��� �ϴ� Ŭ������ �ʿ��ϴ�.
2. 8080 ��Ʈ�� ����ϴ� http������ ����� ���ο� post request�� ���� ������ body �� merge.log ���Ͽ� �����̴� �۾��� �ϴ� Ŭ������ �ʿ��ϴ�.
3. �� �۾��� ���ÿ� ����� �� �־�� �ϰ�, Main Ŭ�������� ���� Ŭ������ ������ �� �־�� �Ѵ�.
4. ��� �۾��� thread safe �ؾ� �Ѵ�.
5. jetty ���̺귯���� ����ؾ� �Ѵ�.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // ���� ��� ����
        String tmpFilePath = "tmp.log";
        String mergeFilePath = "merge.log";

        // LogMergeWorker ������ ����
        LogMergeWorker logMergeWorker = new LogMergeWorker(tmpFilePath, mergeFilePath);
        Thread logMergeThread = new Thread(logMergeWorker);
        logMergeThread.start();

        // HttpRequestWorker ������ ����
        int port = 8080;
        HttpRequestWorker httpRequestWorker = new HttpRequestWorker(mergeFilePath);
        Thread httpRequestThread = new Thread(httpRequestWorker);
        httpRequestThread.start();

        // Jetty ���� ����
        Server server = new Server(port);
        server.setHandler(new RequestHandler(httpRequestWorker));
        server.start();
        server.join();
    }
}
