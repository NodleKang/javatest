package com.lgcns.test.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyHttp {

    /**
     * 객체를 JSON으로 변환해서 HttpServletResponse 응답 본문에 씁니다. (객체를 JSON으로 변환하는 데 실패하면 IOException이 발생)
     */
    public static void writeJsonObjectToHttpResponse(HttpServletResponse resp, Object obj) throws IOException {
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        Gson gson = new GsonBuilder().serializeNulls().create();
        resp.getWriter().write(gson.toJson(obj));
    }

    /**
     * HttpServletRequest의 body를 JsonObject로 변환
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
     * 요청 본문을 String 객체로 반환(요청 본문이 JSON 형식이 아니거나 요청 본문이 없으면 null을 반환)
     */
    private String readRequestBodyAsJsonString(HttpServletRequest req) {

        try {
            // HTTP 요청 본문을 읽기 위한 BufferedReader 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            StringBuilder requestBody = new StringBuilder();
            String line;

            // BufferedReader를 사용하여 HTTP 요청 본문을 읽어옴
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
