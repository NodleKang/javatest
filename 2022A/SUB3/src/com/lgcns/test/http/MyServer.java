package com.lgcns.test.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import com.lgcns.test.http.servlet.*;
import java.util.*;

/**
 * jetty 웹서버를 생성하고 실행하는 클래스
 */
public class MyServer extends Thread {

    private static volatile MyServer instance = null;
    private Server server;
    private int port;
    private Map<String, String> routeMap;

    /**
     * 생성자
     * @param port
     */
    private MyServer(int port) {
        this.port = port;
    }

    private void setRouteMap(Map<String, String> routeMap) {
        this.routeMap = routeMap;
    }

    /**
     * 싱글톤 패턴을 따르며, 처음 호출될 때만 HttpServer 인스턴스를 생성하고 스레드로 실행합니다.
     * @param port
     * @return
     */
    public static MyServer getInstance(int port, Map<String, String> routeMap) {
        if (instance == null) {
            synchronized (MyServer.class) {
                System.out.printf("HttpServer:%d started.\n", port);
                instance = new MyServer(port);
                instance.setRouteMap(routeMap);
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

        // 루트 컨텍스트 경로를 "/"로 설정
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // 라우트 설정
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ProxyServlet proxyServlet = new ProxyServlet();
            proxyServlet.setTargetPath(value);
            context.addServlet(new ServletHolder(proxyServlet), key+"/*");
        }

        // 핸들러 설정
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(context);
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
