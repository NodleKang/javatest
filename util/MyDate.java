package test.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class MyDate {

    /**
     * 현재 시각을 LocalDateTime 객체로 반환
     */
    public static LocalDateTime now() {
        // 현재 시각 가져오기
        LocalDateTime now = LocalDateTime.now();
        return now;
    }

    /**
     * 시각을 문자열로 반환 (추천 포멧: "yyyy-MM-dd HH:mm:ss")
     */
    public static String convertDateToString(LocalDateTime localDateTime, String format) {
        // 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        // 문자열로 변환
        String formattedDateTime = localDateTime.format(formatter);

        return formattedDateTime;
    }

    /**
     * String을 정해진 format에 맞춰서 LocalDataTime 타입으로 변환
     */
    public static LocalDateTime convertStringToDate(String dateTimeString, String format) {
        // 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        // 문자열을 시각으로 변환
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);

        return localDateTime;
    }

    /**
     * LocalDateTime을 ZonedDateTime으로 변환
     */
    public static ZonedDateTime convertLocalDateTimeToZonedDateTime(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return ZonedDateTime.of(localDateTime, zoneId);
    }

    /**
     * String으로 된 날짜에서 요일을 String으로 뽑아내기 (날짜포멧은 "yyyy-MM-dd HH:mm:ss"로 고정)
     */
    public static String getWeekdayDisplayName(String dateTimeString) {
        LocalDateTime localDateTime = convertStringToDate(dateTimeString, "yyyy-MM-dd HH:mm:ss");
        return getWeekdayDisplayName(localDateTime);
    }

    /**
     * String으로 된 날짜에서 요일을 String으로 뽑아내기 (날짜포멧도 인자로 받기)
     */
    public static String getWeekdayDisplayName(String dateTimeString, String format) {
        LocalDateTime localDateTime = convertStringToDate(dateTimeString, format);
        return getWeekdayDisplayName(localDateTime);
    }

    /**
     * LocalDateTime에서 요일을 String으로 뽑아내기
     */
    public static String getWeekdayDisplayName(LocalDateTime localDateTime) {
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        int weekday = dayOfWeek.getValue(); // 1 ~ 7 = 월 ~ 일
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US); // Locale.KOREAN
    }

}
