package com.lgcns.test;

import com.lgcns.test.conf.ProxyConfigReader;
import com.lgcns.test.http.MyServer;
import com.lgcns.test.util.MyFile;

import java.util.Map;

public class RunManager {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		testOnHttp(args[0]);
	}

	/**
	 * HTTP ��û ������� �׽�Ʈ�ϱ�
	 */
	public static void testOnHttp(String configFile) {

		// ���α׷� ���� �� proxy.txt ������ �ʱ�ȭ
		MyFile.cleanFile("proxy.txt");

		// Proxy-n.txt ���Ͽ��� Route ������ �о��
		Map<Integer, Map<String, String>> routeMap = new ProxyConfigReader().readConfigFile(configFile);

		// �о�� Route ������ ������� Http Server�� �����ϰ� ����
		for (int port : routeMap.keySet()) {
			// �̱��� ���� Http Server ����
			MyServer.getInstance(port, routeMap.get(port));
		}

		// ���α׷��� ���� �ʵ��� ����
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
