package com.lgcns.test.analyzer;

import com.lgcns.test.util.MyFile;

import java.util.*;

/**
 * 로그 파일을 읽어서 트리 구조로 변환하는 클래스
 * Flat List를 순회하며 노드를 계층적인 부모-자식 관계로 구성하는 것이 주요 기능입니다.
 */
public class HistoryAnalyzer {

    private static final String DELIMITER = ",";

    public static void main(String[] args) {

        String requestId = "1000";

        // 로그 파일 읽기
        String filePath = MyFile.getCurrentDirectoryFullPath()+"\\2022A\\SUB4\\logs\\history.txt";;
        List<String> lines = MyFile.readFileToList(filePath, "UTF-8");

        // requestId에 해당하는 로그만 추출하여 트리 구조로 변환
        ServiceNode serviceNode = populateTree(requestId, lines);
        serviceNode.getParentName();
    }

    /**
     * requestId에 해당하는 로그만 추출하여 트리 구조로 변환
     */
    public static ServiceNode populateTree(String requestId, List<String> lines) {

        // 파일 내용을 파싱하여 Map으로 변환 (key는 requestId이고, value는 LogEntry의 List)
        Map<String, LinkedList<ServiceNode>> logMap = parseLogEntries(lines);

        // requestId를 기준으로 ServiceNode List를 가져옴
        LinkedList<ServiceNode> flatList = logMap.get(requestId);

        // Id를 기준으로 ServiceNode를 빠르게 액세스 할 수 있도록 Map으로 변환
        Map<String, ServiceNode> serviceNodeMap = new HashMap<>();
        for (ServiceNode serviceNode : flatList) {
            serviceNodeMap.put(serviceNode.getParentName(), serviceNode);
        }

        // Flat List를 순회하며 노드를 계층적인 부모-자식 관계로 구성
        /*
         * Flat List 구조
         * requestID,timestamp(epoch_millis),source,target,status
         * 1000,1689405783,5001/front,8081/front,200
         */
        ServiceNode root = null;
        for (ServiceNode serviceNode : flatList) {
            if (root == null) {
                root = serviceNode;
            }
            String parentName = serviceNode.getParentName();
            String childName = serviceNode.getChildName();
            if ( parentName != null ) {
                ServiceNode parentNode = serviceNodeMap.get(parentName);
                ServiceNode childNode = serviceNodeMap.get(childName);

                if (parentNode != null && childNode != null) {
                    parentNode.getServices().add(childNode);
                }
            }
        }

        return root;
    }

    /**
     * 로그에 있는 항목들을 파싱해서 Map으로 반환합니다.
     * Map의 key는 requestId이고, value는 ServiceNode의 List입니다.
     */
    private static Map<String, LinkedList<ServiceNode>> parseLogEntries(List<String> lines) {
        Map<String, LinkedList<ServiceNode>> logMap = new HashMap<>();

        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            if (parts.length == 4) {
                String requestId = parts[0];
                String parentName = parts[1];
                String childName = parts[2];
                String status = parts[3];

                ServiceNode serviceNode = new ServiceNode(parentName, childName, status);

                logMap.putIfAbsent(requestId, new LinkedList<>());
                logMap.get(requestId).add(serviceNode);
            }
        }

        return logMap;
    }

}
