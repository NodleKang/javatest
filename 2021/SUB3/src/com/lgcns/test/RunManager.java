package com.lgcns.test;

public class RunManager {

	public static void main(String[] args) {
		testOnHttp();
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
