package com.lgcns.test;

import com.lgcns.test.http.*;
import com.lgcns.test.http.handler.QueueInfo;
import com.lgcns.test.util.MyJson;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class RunManager {

	public static void main(String[] args) {
		try {
			testOnHttp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * HTTP ��û ������� �׽�Ʈ�ϱ�
	 */
	public static void testOnHttp() throws Exception {

		// HttpClient �ۼ��� ����
		HttpClient client = new HttpClient();
		client.start();

		// HttpClient�� ���� GET Request �ۼ�, timeout ����
		Request request = client.newRequest("http://127.0.0.1:8080/queueInfo")
				.method(HttpMethod.GET)
				.timeout(0, TimeUnit.MINUTES)
				.idleTimeout(0, TimeUnit.MINUTES);

		// Request ������ ����ޱ�
		ContentResponse response = request.send();

		// Response ������ String ������ �ޱ�
		String respStr = "";
		if (response.getStatus() == 200) {
			respStr = response.getContentAsString();
		}

		QueueInfo queueInfo = (QueueInfo) MyJson.convertStringToObject(respStr, QueueInfo.class);
		for (String uri : queueInfo.getInputQueueURIs()) {
			System.out.println("Input Queue URI: " + uri);
		}

		client.stop();
	}

}
