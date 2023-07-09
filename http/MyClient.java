package test.http;

import java.util.*;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class MyClient {

    private final HttpClient httpClient;

    /**
     * HttpClient 생성자
     * @throws Exception
     */
    public MyClient() throws Exception {

        // HttpClient의 스레드 풀을 설정합니다.
        // 스레드 풀의 크기는 동시에 처리할 수 있는 요청의 개수를 의미합니다.
        // 스레드 풀의 크기를 설정하지 않으면 기본값으로 200이 설정됩니다.
        this.httpClient = new HttpClient();
        this.httpClient.setExecutor(new QueuedThreadPool(100));

        // 리다아렉트 활성화 여부를 설정합니다. (기본값: true = 활성화)
        this.httpClient.setFollowRedirects(true);

        // Connection timeout 설정(단위: ms) 제한 없음
        this.httpClient.setConnectTimeout(0);

        // 읽기 시간 초과 설정(단위: ms)  제한 없음
        this.httpClient.setIdleTimeout(0);

        // HttpClient를 시작합니다.
        this.httpClient.start();
    }

    /**
     * HttpClient 생성자 (Connection timeout, Idle timeout 설정)
     * @param connectionTimeoutMs
     * @param idleTimeoutMs
     * @throws Exception
     */
    public MyClient(int connectionTimeoutMs, int idleTimeoutMs) throws Exception {

        HttpClient httpClientTemp;

        // SSL 컨텍스트 팩토리를 설정하지 않는 경우에는 아래와 같이 설정합니다.
        // httpClientTemp = new HttpClient();

        // SSL 컨텍스트 팩토리를 설정한 httpClientTemp를 사용하려면 아래와 같이 설정합니다.
        // SSL 컨텍스트 팩토리는 SSL/TLS 연결을 사용하는 경우에만 설정합니다.
        httpClientTemp = new HttpClient(new SslContextFactory.Client());

        // 트랜스포트 설정한 httpClientTemp를 사용하려면 아래와 같이 설정합니다.
        // HTTP 트랜스포트는 HTTP, HTTPS, HTTP/2 등을 설정할 수 있습니다.
        // HTTP 트랜스포트를 설정하지 않으면 기본값으로 HTTP/1.1이 설정됩니다.
        // httpClientTemp = new HttpClient(new HttpClientTransportOverHTTP());

        // HttpClient의 스레드 풀을 설정합니다.
        // 스레드 풀의 크기는 동시에 처리할 수 있는 요청의 개수를 의미합니다.
        // 스레드 풀의 크기를 설정하지 않으면 기본값으로 200이 설정됩니다.
        this.httpClient = httpClientTemp;
        this.httpClient.setExecutor(new QueuedThreadPool(100));

        // HttpClient의 트랜스포트를 설정합니다.
        // 트랜스포트는 HTTP, HTTPS, HTTP/2 등을 설정할 수 있습니다.
        // 트랜스포트를 설정하지 않으면 기본값으로 HTTP/1.1이 설정됩니다.
        // httpClient.setTransport(new HttpClientTransportOverHTTP2());

        // 리다아렉트 활성화 여부를 설정합니다. (기본값: true = 활성화)
        this.httpClient.setFollowRedirects(true);

        // Connection timeout 설정(단위: ms)
        this.httpClient.setConnectTimeout(connectionTimeoutMs);

        // 읽기 시간 초과 설정(단위: ms)
        this.httpClient.setIdleTimeout(idleTimeoutMs);

        // HttpClient를 시작합니다.
        this.httpClient.start();
    }

    /**
     * HttpClient 를 통해 GET 요청을 보내고 응답을 받습니다.
     * @param url
     * @return
     * @throws Exception
     */
    public String get(String url) throws Exception {
        Request request = httpClient.newRequest(url)
                .method(HttpMethod.GET)
                .version(HttpVersion.HTTP_1_1);
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 POST 요청을 보내고 응답을 받습니다.
     * @param url
     * @return
     * @throws Exception
     */
    public String post(String url, String body) throws Exception {
        Request request = httpClient.newRequest(url)
                .method(HttpMethod.POST)
                .version(HttpVersion.HTTP_1_1);
        request.content(new StringContentProvider(body));
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 PUT 요청을 보내고 응답을 받습니다.
     * @param url
     * @return
     * @throws Exception
     */
    public String put(String url, String body) throws Exception {
        Request request = httpClient.newRequest(url)
                .method(HttpMethod.PUT)
                .version(HttpVersion.HTTP_1_1);
        request.content(new StringContentProvider(body));
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 DELETE 요청을 보내고 응답을 받습니다.
     * @param url
     * @return
     * @throws Exception
     */
    public String delete(String url) throws Exception {
        Request request = httpClient.newRequest(url)
                .method(HttpMethod.DELETE)
                .version(HttpVersion.HTTP_1_1);
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 GET 요청을 보내고 응답을 받습니다. (헤더, 프로퍼티 설정)
     * @param url
     * @param headers
     * @param properties
     * @return
     * @throws Exception
     */
    public String get(String url, Map<String, String> headers, Map<String, String> properties) throws Exception {
        Request request = createRequest(HttpMethod.GET, url, headers, properties);
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 POST 요청을 보내고 응답을 받습니다. (바디, 헤더, 프로퍼티 설정)
     * @param url
     * @param body
     * @param headers
     * @param properties
     * @return
     * @throws Exception
     */
    public String post(String url, String body, Map<String, String> headers, Map<String, String> properties) throws Exception {
        Request request = createRequest(HttpMethod.POST, url, headers, properties);
        request.content(new StringContentProvider(body));
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 PUT 요청을 보내고 응답을 받습니다. (바디, 헤더, 프로퍼티 설정)
     * @param url
     * @param body
     * @param headers
     * @param properties
     * @return
     * @throws Exception
     */
    public String put(String url, String body, Map<String, String> headers, Map<String, String> properties) throws Exception {
        Request request = createRequest(HttpMethod.PUT, url, headers, properties);
        request.content(new StringContentProvider(body));
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 통해 DELETE 요청을 보내고 응답을 받습니다. (헤더, 프로퍼티 설정)
     * @param url
     * @param headers
     * @param properties
     * @return
     * @throws Exception
     */
    public String delete(String url, Map<String, String> headers, Map<String, String> properties) throws Exception {
        Request request = createRequest(HttpMethod.DELETE, url, headers, properties);
        ContentResponse response = request.send();
        if (response.getStatus() == 200) {
            return response.getContentAsString();
        }
        return null;
    }

    /**
     * HttpClient 를 종료합니다.
     * @throws Exception
     */
    public void stop() throws Exception {
        httpClient.stop();
    }

    /**
     * HttpClient 를 통해 보낼 Request를 생성합니다.
     * @param method
     * @param url
     * @param headers
     * @param properties
     * @return
     */
    private Request createRequest(
            HttpMethod method,
            String url,
            Map<String, String> headers,
            Map<String, String> properties
    ) {
        Request request = httpClient.newRequest(url)
                .method(method)
                .version(HttpVersion.HTTP_1_1);

        // Headers 설정
        if (headers != null) {
            HttpFields httpFields = request.getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpFields.put(entry.getKey(), entry.getValue());
            }
        }

        // Properties 설정
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                request.param(entry.getKey(), entry.getValue());
            }
        }

        return request;
    }
}
