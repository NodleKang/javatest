import org.eclipse.jetty.server.Server;
/*
나는 아래 조건을 만족하는 java로 프로그램을 만들어야 한다. 코드를 작성해달라.
1. tmp.log 파일에 새로운 내용이 추가될 때마다 그 내용을 읽어서 merge.log 파일에 덧붙이는 작업을 하는 클래스가 필요하다.
2. 8080 포트를 사용하는 http서버를 띄워서 새로운 post request가 들어올 때마다 body 를 merge.log 파일에 덧붙이는 작업을 하는 클래스가 필요하다.
3. 두 작업은 동시에 실행될 수 있어야 하고, Main 클래스에서 각각 클래스를 실행할 수 있어야 한다.
4. 모든 작업은 thread safe 해야 한다.
5. jetty 라이브러리를 사용해야 한다.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 파일 경로 설정
        String tmpFilePath = "tmp.log";
        String mergeFilePath = "merge.log";

        // LogMergeWorker 쓰레드 시작
        LogMergeWorker logMergeWorker = new LogMergeWorker(tmpFilePath, mergeFilePath);
        Thread logMergeThread = new Thread(logMergeWorker);
        logMergeThread.start();

        // HttpRequestWorker 쓰레드 시작
        int port = 8080;
        HttpRequestWorker httpRequestWorker = new HttpRequestWorker(mergeFilePath);
        Thread httpRequestThread = new Thread(httpRequestWorker);
        httpRequestThread.start();

        // Jetty 서버 시작
        Server server = new Server(port);
        server.setHandler(new RequestHandler(httpRequestWorker));
        server.start();
        server.join();
    }
}
