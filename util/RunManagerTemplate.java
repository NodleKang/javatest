package util;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class RunManagerTemplate {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * �ܼ��Է� ������� �׽�Ʈ�ϱ�
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
     * HTTP ��û ������� �׽�Ʈ�ϱ�
     */
    public static void testOnHttp() {
        int port = 8080;
        // �̱��� ���� Http Server ����
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
