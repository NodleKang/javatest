package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyHttp {

    /**
     * ��ü�� JSON���� ��ȯ�ؼ� HttpServletResponse ���� ������ ���ϴ�. (��ü�� JSON���� ��ȯ�ϴ� �� �����ϸ� IOException�� �߻�)
     */
    public static void writeJsonObjectToHttpResponse(HttpServletResponse resp, Object obj) throws IOException {
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        Gson gson = new GsonBuilder().serializeNulls().create();
        resp.getWriter().write(gson.toJson(obj));
    }

    /**
     * HttpServletRequest�� body�� JsonObject�� ��ȯ
     */
    public static Object readRequestBodyAsJsonObject(HttpServletRequest req, Class c) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = req.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String jsonBody = requestBody.toString();
        Gson gson = new GsonBuilder()
                .serializeNulls()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                //.setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        Object o = gson.fromJson(jsonBody, c);
        return o;
    }

    /**
     * ��û ������ String ��ü�� ��ȯ(��û ������ JSON ������ �ƴϰų� ��û ������ ������ null�� ��ȯ)
     */
    private String readRequestBodyAsJsonString(HttpServletRequest req) {

        try {
            // HTTP ��û ������ �б� ���� BufferedReader ����
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            StringBuilder requestBody = new StringBuilder();
            String line;

            // BufferedReader�� ����Ͽ� HTTP ��û ������ �о��
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            reader.close();

            return requestBody.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
