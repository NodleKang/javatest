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
	 * HTTP 요청 기반으로 테스트하기
	 */
	public static void testOnHttp() throws Exception {

		// HttpClient 작성과 시작
		HttpClient client = new HttpClient();
		client.start();

		// HttpClient로 보낼 GET Request 작성, timeout 없음
		Request request = client.newRequest("http://127.0.0.1:8080/queueInfo")
				.method(HttpMethod.GET)
				.timeout(0, TimeUnit.MINUTES)
				.idleTimeout(0, TimeUnit.MINUTES);

		// Request 보내고 응답받기
		ContentResponse response = request.send();

		// Response 본문을 String 변수로 받기
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
