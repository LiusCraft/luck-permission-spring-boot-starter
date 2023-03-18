package com.liuscraft.luckpermission.entity;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author LiusCraft
 * @date 2023/3/5 15:44
 */
public class LuckVerifyEntity {
    private String route;
    private String[] routes;
    private String description;
    private String requestMethod;
    private Boolean ignore = false;
    private List<String> pathVariables = new ArrayList<>();

    public LuckVerifyEntity(String route, RequestMethod requestMethod) {
        initRoute(route);
        if (requestMethod == null) this.requestMethod = "all";
        else this.requestMethod = requestMethod.name();
    }

    public LuckVerifyEntity(String route, String requestMethod) {
        initRoute(route);
        if (requestMethod == null) this.requestMethod = "all";
        else this.requestMethod = requestMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIgnore() {
        this.ignore = true;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    private List<String> getPathVariables() {
        return pathVariables;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRoute() {
        return route;
    }

    public String getMethodRoute() {
        return getRequestMethod()+":"+getRoute();
    }

    private String[] getRoutes() {
        return routes;
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

        if (getPathVariables().size()>0) {
            String[] targets = route.split("/");
            if (targets.length != getRoutes().length) return false;
            int len = 0;
            for (int i = 0; i < targets.length; i++) {
                if (getPathVariables().contains(getRoutes()[i])) {
                    len++;
                    if (len>getPathVariables().size()) return false;
                    continue;
                }
                if(!targets[i].equals(getRoutes()[i])) return false;
            }
            return true;
        }else return getRoute().equalsIgnoreCase(route);

    }

    /**
     * 验证该校验是否支持某方法
     * @param method 请求方法
     * @return true支持 or false不支持
     */
    public boolean checkMethod(String method) {
        return getRequestMethod().equalsIgnoreCase(method);
    }

    public boolean checkPermission(LuckPermission luckPermission) {
        return luckPermission.getMethodRoute().equalsIgnoreCase(this.getMethodRoute());
    }

    public boolean checkPermissions(List<LuckPermission> luckPermissions) {
        for (LuckPermission luckPermission : luckPermissions) {
            if (checkPermission(luckPermission)) return true;
        }
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        return getMethodRoute().equalsIgnoreCase(o.toString());
    }

    @Override
    public String toString() {
        return getMethodRoute();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestMethod(), getRoute());
    }
}
