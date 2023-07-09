/*
 * Gson을 사용해서 ZonedDateTime을 직렬화/역직렬화하면 오류가 발생합니다.
 * - java.lang.RuntimeException: Failed to invoke java.time.ZoneId() with no args 오류 발생의 예
 * public void test() {
 *  String json = new GsonBuilder().create().toJson(ZonedDateTime.now());
 *  ZonedDateTime dateTime = new GsonBuilder().create().fromJson(json, ZonedDateTime.class);
 * }
 *
 * 오류 해결을 위해 ZonedDateTime의 직렬화/역직렬화를 별도로 만들어줘야 합니다. = MyJsonZonedDateTime 클래스
 *
 * 이후 GsonBuilder를 생성할 때, ZonedDateTime을 위한 타입어댑터에 만들어 놓은 MyJsonZonedDateTime 클래스를 등록합니다.
 * private static GsonBuilder getGsonBuilder() {
 *  final GsonBuilder builder = new GsonBuilder();
 *  builder.registerTypeAdapter(ZonedDateTime.class, new MyJsonZonedDateTime());
 *  return builder;
 * }
 */
package test.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MyJsonZonedDateTime implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            final String dateTimeString = json.getAsString();
            return ZonedDateTime.parse(dateTimeString);
        } catch (Exception e) {
            throw new JsonParseException("Failed to new instance", e);
        }
    }

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_INSTANT));
    }
}
