package util;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class RunManagerTemplate {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * 콘솔입력 기반으로 테스트하기
     */
    public static void testOnConsole() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            // read line from console util user input "exit"
            String line = scanner.nextLine();
            if (line.equals("exit")) {
                break;
            }

        }
    }

    /**
     * HTTP 요청 기반으로 테스트하기
     */
    public static void testOnHttp() {
        int port = 8080;
        // 싱글톤 패턴 Http Server 실행
        MyServer server = MyServer.getInstance(port);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
