package com.lgcns.test.analyzer;

import com.lgcns.test.util.MyFile;

import java.util.*;

/**
 * �α� ������ �о Ʈ�� ������ ��ȯ�ϴ� Ŭ����
 * Flat List�� ��ȸ�ϸ� ��带 �������� �θ�-�ڽ� ����� �����ϴ� ���� �ֿ� ����Դϴ�.
 */
public class HistoryAnalyzer {

    private static final String DELIMITER = ",";

    public static void main(String[] args) {

        String requestId = "1000";

        // �α� ���� �б�
        String filePath = MyFile.getCurrentDirectoryFullPath()+"\\2022A\\SUB4\\logs\\history.txt";;
        List<String> lines = MyFile.readFileToList(filePath, "UTF-8");

        // requestId�� �ش��ϴ� �α׸� �����Ͽ� Ʈ�� ������ ��ȯ
        ServiceNode serviceNode = populateTree(requestId, lines);
        serviceNode.getParentName();
    }

    /**
     * requestId�� �ش��ϴ� �α׸� �����Ͽ� Ʈ�� ������ ��ȯ
     */
    public static ServiceNode populateTree(String requestId, List<String> lines) {

        // ���� ������ �Ľ��Ͽ� Map���� ��ȯ (key�� requestId�̰�, value�� LogEntry�� List)
        Map<String, LinkedList<ServiceNode>> logMap = parseLogEntries(lines);

        // requestId�� �������� ServiceNode List�� ������
        LinkedList<ServiceNode> flatList = logMap.get(requestId);

        // Id�� �������� ServiceNode�� ������ �׼��� �� �� �ֵ��� Map���� ��ȯ
        Map<String, ServiceNode> serviceNodeMap = new HashMap<>();
        for (ServiceNode serviceNode : flatList) {
            serviceNodeMap.put(serviceNode.getParentName(), serviceNode);
        }

        // Flat List�� ��ȸ�ϸ� ��带 �������� �θ�-�ڽ� ����� ����
        /*
         * Flat List ����
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
     * �α׿� �ִ� �׸���� �Ľ��ؼ� Map���� ��ȯ�մϴ�.
     * Map�� key�� requestId�̰�, value�� ServiceNode�� List�Դϴ�.
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
