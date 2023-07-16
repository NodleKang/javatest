package com.lgcns.test;

import com.lgcns.test.conf.ProxyConfigReader;
import com.lgcns.test.http.MyServer;

import java.util.Map;
import java.util.Scanner;

public class RunManager {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		testOnHttp(args[0]);
	}

	/**
	 * HTTP 요청 기반으로 테스트하기
	 */
	public static void testOnHttp(String configFile) {

		// Proxy-n.txt 파일에서 Route 정보를 읽어옴
		Map<Integer, Map<String, String>> routeMap = new ProxyConfigReader().readConfigFile(configFile);

		// 읽어온 Route 정보를 기반으로 Http Server를 생성하고 실행
		for (int port : routeMap.keySet()) {
			// 싱글톤 패턴 Http Server 실행
			MyServer.getInstance(port, routeMap.get(port));
		}

		// 프로그램이 죽지 않도록 설정
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
