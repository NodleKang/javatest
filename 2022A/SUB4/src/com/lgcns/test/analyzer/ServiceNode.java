package com.lgcns.test.analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * 서비스 정보를 트리구조로 담기 위한 클래스
 */
public class ServiceNode {
    private String parentName;
    private String childName;
    private String respStatus;
    private List<ServiceNode> services;

    public ServiceNode(String parentName, String childName, String respStatus) {
        this.parentName = parentName;
        this.childName = childName;
        this.respStatus = respStatus;
        this.services = new ArrayList<>();
    }

    public String getParentName() {
        return parentName;
    }

    public String getChildName() {
        return childName;
    }

    public String getRespStatus() {
        return respStatus;
    }

    public List<ServiceNode> getServices() {
        return services;
    }

}
