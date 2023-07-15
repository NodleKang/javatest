package util;

import com.google.gson.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

public class MyJson {

    /**
     * 객체를 JSON으로 변환해서 HttpServletResponse 응답 본문에 씁니다. (객체를 JSON으로 변환하는 데 실패하면 IOException이 발생)
     */
    public static void writeJsonToHttpResponse(HttpServletResponse resp, Object obj) throws IOException {
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        Gson gson = new GsonBuilder().serializeNulls().create();
        resp.getWriter().write(gson.toJson(obj));
    }

    /**
     * HttpServletRequest의 body를 JsonObject로 변환
     */
    public static Object convertHttpReqBodyToObject(HttpServletRequest req, Class c) throws IOException {
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
     * String을 JsonObject로 변환
     */
    public static Object convertStringToObject(String content, Class c) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                //.setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        try {
            Object o = gson.fromJson(content, c);
            return o;
        } catch (Exception e) {
            System.out.println("MyJson.convertStringToObject() Exception: " + e.getMessage());
        }
        return null;
    }

    /**
     * BufferedReader를 JsonObject로 변환
     * @param br
     * @return
     */
    public static JsonObject convertBufferedReaderToJsonObject(BufferedReader br) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        JsonObject jsonObj = gson.fromJson(br, JsonObject.class);
        return jsonObj;
    }

    /**
     * BufferedReader를 JsonArray로 변환
     * @param br
     * @return
     */
    public static JsonArray convertBufferedReaderToJsonArray(BufferedReader br) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        JsonArray jsonArray = gson.fromJson(br, JsonArray.class);
        return jsonArray;
    }

    /**
     * String을 JsonObject로 변환
     */
    public static JsonObject convertStringToJsonObject(String jsonStr) {
        Gson gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation() // @Expose 어노테이션이 붙어있지 않은 필드는 JSON을 변환 안 함
                .serializeNulls() // null 값도 JSON 변환
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 날짜 타입을 json으로 담을 때의 형식
                .setPrettyPrinting() // gson 데이터를 보기 좋게 출력
                .create();
        JsonObject jsonObj = gson.fromJson(jsonStr, JsonObject.class);
        return jsonObj;
    }

    /**
     * JsonObject를 String으로 변환
     */
    public static String convertJsonObjectToString(JsonObject jsonObj) {
        Gson gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation() // @Expose 어노테이션이 붙어있지 않은 필드는 JSON을 변환 안 함
                .serializeNulls() // null 값도 JSON 변환
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 날짜 타입을 json으로 담을 때의 형식
                .setPrettyPrinting() // gson 데이터를 보기 좋게 출력
                .create();
        String jsonStr = gson.toJson(jsonObj);
        return jsonStr;
    }

    /**
     * String을 JsonArray로 변환
     */
    public static JsonArray convertStringToJsonArray(String jsonStr) {
        Gson gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation() // @Expose 어노테이션이 붙어있지 않은 필드는 JSON을 변환 안 함
                .serializeNulls() // null 값도 JSON 변환
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 날짜 타입을 json으로 담을 때의 형식
                .setPrettyPrinting() // gson 데이터를 보기 좋게 출력
                .create();
        JsonArray jsonArray = gson.fromJson(jsonStr, JsonArray.class);
        return jsonArray;
    }

    /**
     * JsonArray를 String으로 변환
     */
    public static String convertJsonArrayToString(JsonArray jsonArray) {
        Gson gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation() // @Expose 어노테이션이 붙어있지 않은 필드는 JSON을 변환 안 함
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 날짜 타입을 json으로 담을 때의 형식
                .setPrettyPrinting() // gson 데이터를 보기 좋게 출력
                .create();
        String jsonStr = gson.toJson(jsonArray);
        return jsonStr;
    }


    /**
     * JsonArray를 LinkedList<String>으로 변환
     */
    public static LinkedList<String> convertJsonArrayToStringList(JsonArray jsonArray) {
        LinkedList<String> list = new LinkedList<String>();
        for (JsonElement element: jsonArray) {
            list.add(element.getAsString());
        }
        return list;
    }

}
