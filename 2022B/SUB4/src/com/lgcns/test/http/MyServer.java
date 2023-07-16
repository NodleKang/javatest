package com.lgcns.test.http;

import com.lgcns.test.http.handler.MyHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * jetty 웹서버를 생성하고 실행하는 클래스
 */
public class MyServer extends Thread {

    private static volatile MyServer instance = null;
    private Server server;
    private int port;

    /**
     * 생성자
     * @param port
     */
    private MyServer(int port) {
        this.port = port;
    }
    /**
     * 싱글톤 패턴을 따르며, 처음 호출될 때만 HttpServer 인스턴스를 생성하고 스레드로 실행합니다.
     * @param port
     * @return
     */
    public static MyServer getInstance(int port) {
        if (instance == null) {
            synchronized (MyServer.class) {
                instance = new MyServer(port);
                instance.setName("HttpServer");
                instance.setDaemon(true);
                instance.start();
            }
        }
        return instance;
    }

    /**
     * 서버를 구성하고 시작합니다.
     */
    @Override
    public void run() {

        // 서버의 스레드풀 크기 설정
        int maxThreads = 100;
        int minThreads = 2;
        server = new Server(new QueuedThreadPool(maxThreads, minThreads));

        // 서버의 포트 설정
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new ServerConnector[] { connector });

        // 핸들러 설정
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(new MyHandler());
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 서버를 중지합니다.
     */
    public void stopServer() {
        try {
            if (instance != null) {
                instance.interrupt();
                instance = null;
                System.out.println("HttpServer stopped.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
