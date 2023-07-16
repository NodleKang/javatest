package com.lgcns.test.conf;

import com.lgcns.test.util.MyFile;
import com.lgcns.test.util.MyJson;

import java.util.*;

public class ProxyConfigReader {

    public static void main(String[] args) {

        Map<Integer, Map<String, String>> routeMap = new HashMap<>();

        String path = MyFile.getCurrentDirectoryFullPath() + "/2022A/SUB3/";
        String fileNames[] = MyFile.findFiles(path, "Proxy", "", false);

        List<ProxyConfig> proxyConfigList = new ArrayList<>();
        for (String fileName : fileNames) {
            new ProxyConfigReader().readConfigFile(fileName);
        }

    }

    public Map<Integer, Map<String, String>> readConfigFile(String fileName) {

        Map<Integer, Map<String, String>> routeMap = new HashMap<>();

        String content = MyFile.readFileToString(fileName);
        ProxyConfig proxyConfig = (ProxyConfig) MyJson.convertStringToObject(content, ProxyConfig.class);

        int port = proxyConfig.getPort();
        routeMap.putIfAbsent(port, new HashMap<>());
        List<ProxyConfig.Route> routes = proxyConfig.getRoutes();
        for (ProxyConfig.Route route : routes) {
            routeMap.get(port).put(route.getPathPrefix(), route.getUrl());
        }

        return routeMap;

    }
}
