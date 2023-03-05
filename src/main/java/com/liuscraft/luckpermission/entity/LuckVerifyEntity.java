package com.liuscraft.luckpermission.entity;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author LiusCraft
 * @date 2023/3/5 15:44
 */
public class LuckVerifyEntity {
    private String route;
    private String[] routes;
    private Set<RequestMethod> requestMethods;
    private List<String> pathVariables = new ArrayList<>();

    public LuckVerifyEntity(String route, Set<RequestMethod> requestMethods) {
        initRoute(route);
        this.requestMethods = requestMethods;
    }

    public List<String> getPathVariables() {
        return pathVariables;
    }

    public Set<RequestMethod> getRequestMethods() {
        return requestMethods;
    }

    public String getRoute() {
        return route;
    }

    public String[] getRoutes() {
        return routes;
    }

    public void addMethods(Set<RequestMethod> requestMethods) {
        this.requestMethods.addAll(requestMethods);
    }

    private void initRoute(String route) {
        this.routes = route.split("/");
        for (String s : routes) {
            if (s.length()>0 && s.charAt(0)=='{' && s.charAt(s.length()-1) == '}')
                this.pathVariables.add(s);
        }
        this.route = route;
    }

    public boolean checkRoute(String route){

        if (pathVariables.size()>0) {
            String[] targets = route.split("/");
            if (targets.length != this.routes.length) return false;
            int len = 0;
            for (int i = 0; i < targets.length; i++) {
                if (this.pathVariables.contains(this.routes[i])) {
                    len++;
                    if (len>this.pathVariables.size()) return false;
                    continue;
                }
                if(!targets[i].equals(this.routes[i])) return false;
            }
            return true;
        }else return this.route.equalsIgnoreCase(route);

    }

    /**
     * 验证该校验是否支持某方法
     * @param method 请求方法
     * @return true支持 or false不支持
     */
    public boolean checkMethod(String method) {
        for (RequestMethod requestMethod : requestMethods) {
            if (requestMethod.name().equalsIgnoreCase(method)) return true;
        }
        return false;
    }
}
