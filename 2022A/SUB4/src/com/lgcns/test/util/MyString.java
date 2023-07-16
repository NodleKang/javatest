package com.lgcns.test.util;

import java.util.*;

public class MyString {

    // 문자열에 '#'이나 ':'과 같은 여러 구분 기호가 모두 포함되어 있는 경우 delimiter에 정규식을 사용하는 예
    // str.split("[#:]");
    /**
     * 문자열(str)을 주어진 구분자(delimiter)로 분리하여 문자열 배열로 반환
     * @param str
     * @param delimiter
     * @return
     */
    public static String[] splitToStringArray(String str, String delimiter) {
        String[] strArr = str.split(delimiter);
        return strArr;
    }

    /**
     * 문자열(str)을 주어진 구분자(delimiter)로 분리하여 문자열 배열로 반환(빈 문자열 제거 여부 선택)
     * @param str
     * @param delimiter
     * @param removeEmptyString
     * @return
     */
    public static String[] splitToStringArray(String str, String delimiter, boolean removeEmptyString) {
        String[] strArr = str.split(delimiter);
        ArrayList<String> strList = new ArrayList<String>();
        for (String s : strArr) {
            // 빈 문자열 제거 여부가 true이고 빈 문자열인 경우에는 리스트에 추가하지 않음
            if (removeEmptyString && s.isEmpty()) {
                continue;
            }
            strList.add(s);
        }
        strArr = strList.toArray(new String[strList.size()]);
        return strArr;
    }

    /**
     * 문자열(str)을 주어진 구분자(delimiter)로 분리하여 순서가 있는 문자열 리스트로 반환
     * @param str
     * @param delimiter
     * @return
     */
    public static LinkedList<String> splitToLinkedList(String str, String delimiter) {
        String[] strArr = str.split(delimiter);
        LinkedList<String> strList = new LinkedList<String>();
        for (String s : strArr) {
            strList.add(s);
        }
        return strList;
    }

    /**
     * 문자열(str)을 주어진 구분자(delimiter)로 분리하여 순서가 있는 문자열 리스트로 반환(빈 문자열 제거 여부 선택)
     * @param str
     * @param delimiter
     * @param removeEmptyString
     * @return
     */
    public static LinkedList<String> splitToLinkedList(String str, String delimiter, boolean removeEmptyString) {
        String[] strArr = str.split(delimiter);
        LinkedList<String> strList = new LinkedList<String>();
        for (String s : strArr) {
            // 빈 문자열 제거 여부가 true이고 빈 문자열인 경우에는 리스트에 추가하지 않음
            if (removeEmptyString && s.isEmpty()) {
                continue;
            }
            strList.add(s);
        }
        return strList;
    }

    /**
     * 문자열 배열을 이어붙인 문자열 반환
     * @param strArr
     * @param delimiter
     * @return
     */
    public static String joinString(String[] strArr, String delimiter) {
        // String.join() 메소드는 Java 8부터 지원
        String str = String.join(delimiter, strArr);
        return str;
    }

    /**
     * 문자열 리스트를 이어붙인 문자열 반환
     * @param strList
     * @param delimiter
     * @return
     */
    public static String joinString(LinkedList<String> strList, String delimiter) {
        // String.join() 메소드는 Java 8부터 지원
        String str = String.join(delimiter, strList);
        return str;
    }

    /**
     * 문자열 배열을 정렬해서 반환
     * @param strArr
     * @param ascending
     * @return
     */
    public static String[] sortArray(String[] strArr, boolean ascending) {
        Arrays.sort(strArr, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                // 오름차순 정렬
                if (ascending) {
                    return str1.compareTo(str2);
                }
                // 내림차순 정렬
                return str2.compareTo(str1);
            }
        });
        return strArr;
    }

    /**
     * 문자열 리스트를 정렬해서 반환
     * @param strList
     * @param ascending
     * @return
     */
    public static LinkedList<String> sortLinkedList(LinkedList<String> strList, boolean ascending) {
        Collections.sort(strList, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                // 오름차순 정렬
                if (ascending) {
                    return str1.compareTo(str2);
                }
                // 내림차순 정렬
                return str2.compareTo(str1);
            }
        });
        return strList;
    }

    /**
     * 문자열 리스트를 주어진 n번째(0번부터 시작) 문자열을 기준으로 정렬해서 반환
     * 문자열 리스트의 각 문자열은 ','로 구분된 문자열이라고 가정
     */
    public static LinkedList<String> sortByNth(List<String> strList, int n) {
        LinkedList<String> sortedList = new LinkedList<String>(strList);

        Collections.sort(sortedList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] parts1 = o1.split(",");
                String[] parts2 = o2.split(",");
                return parts1[n].compareTo(parts2[n]);
            }
        });
        /*
        // 버블 정렬
        for (int i = 0; i < sortedList.size() - 1; i++) {
            for (int j = 0; j < sortedList.size() - i - 1; j++) {
                String[] parts1 = sortedList.get(j).split(",");
                String[] parts2 = sortedList.get(j + 1).split(",");
                String value1 = parts1[n];
                String value2 = parts2[n];

                if (value1.compareTo(value2) > 0) {
                    // Swap elements
                    String temp = sortedList.get(j);
                    sortedList.set(j, sortedList.get(j + 1));
                    sortedList.set(j + 1, temp);
                }
            }
        }
        */
        return sortedList;
    }

    /**
     * 문자열 리스트를 역순으로 바꿔서 반환
     */
    public static List<String> reverse(List<String> strList) {
        List<String> reversedList = new ArrayList<String>(strList);
        Collections.reverse(reversedList);
        return reversedList;
    }

    /**
     * 문자열 배열을 문자열 리스트로 변환
     * @param strArr
     * @return
     */
    public static LinkedList<String> convertStringArrayToLinkedList(String[] strArr) {
        LinkedList<String> strList = new LinkedList<String>();
        for (String s : strArr) {
            strList.add(s);
        }
        return strList;
    }

    /**
     * 문자열 리스트를 문자열 배열로 반환
     * @param strList
     * @return
     */
    public static String[] convertStringListToStringArray(LinkedList<String> strList) {
        String[] strArr = new String[strList.size()];
        strArr = strList.toArray(strArr);
        return strArr;
    }
}
