package com.lgcns.test;

public class RunManager {

	public static void main(String[] args) {
		testOnHttp();
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
