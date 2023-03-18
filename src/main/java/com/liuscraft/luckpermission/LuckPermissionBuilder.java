package com.liuscraft.luckpermission;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.liuscraft.luckpermission.annotations.LuckVerify;
import com.liuscraft.luckpermission.properties.LuckProperties;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.service.ILuckPermissionService;
import com.liuscraft.luckpermission.service.ILuckRoleService;
import com.liuscraft.luckpermission.utils.ClassUtils;
import com.liuscraft.luckpermission.utils.SqlBaseTemplateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LiusCraft
 * @date 2023/3/5 21:20
 */
@Component
@Log4j2
public class LuckPermissionBuilder {


    private ILuckPermissionService luckPermissionService;
    private LuckProperties luckProperties;
    private JdbcTemplate jdbcTemplate;

    private Set<String> routePaths = new HashSet<>();
    private boolean isLuckController = true;
    public static boolean useAutoTable = false;


    public static List<ISqlParser> getLuckAutoTableHandler() {
        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        useAutoTable = true;
        return Collections.singletonList(dynamicTableNameParser.setTableNameHandlerMap(new HashMap<String, ITableNameHandler>(3) {{
            put("luck_permission", (metaObject, sql, tableName) -> {
                String dynamicTableName = LuckProperties.getLuckProperties().getTable().getPermissionName();
                if(dynamicTableName!=null){
                    return dynamicTableName;
                }else{
                    return tableName;
                }
            });
            put("luck_role", (metaObject, sql, tableName) -> {
                String dynamicTableName = LuckProperties.getLuckProperties().getTable().getRoleName();
                if(dynamicTableName!=null){
                    return dynamicTableName;
                }else{
                    return tableName;
                }
            });
            put("luck_role_permission", (metaObject, sql, tableName) -> {
                String dynamicTableName = LuckProperties.getLuckProperties().getTable().getRolePermissionName();
                if(dynamicTableName!=null){
                    return dynamicTableName;
                }else{
                    return tableName;
                }
            });
        }}));
    }

    public LuckPermissionBuilder(LuckProperties luckProperties,  ILuckPermissionService luckPermissionService, ILuckRoleService luckRoleService) throws Exception {
        this.luckProperties = LuckProperties.getLuckProperties();
        this.luckPermissionService = luckPermissionService;
        this.jdbcTemplate = jdbcTemplate;
        addPackVerifyAnnotation("com.liuscraft.luckpermission.controller");
        HashMap<String, LuckVerifyEntity> luckRoutesMap = (HashMap<String, LuckVerifyEntity>) routesMap.clone();
        isLuckController = false;
        String[] packages = luckProperties.getPackages();
        for (String aPackage : packages) {
            try {
                addPackVerifyAnnotation(aPackage);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("在加载 "+ aPackage+" 时发生了错误...:"+e.getMessage());
            }
        }


    }



    private final HashMap<String, LuckVerifyEntity> routesMap = new HashMap<>();

    public HashMap<String, LuckVerifyEntity> getRoutesMap() {
        return routesMap;
    }
    public Set<String> getRoutePaths() {
        return routePaths;
    }

    public List<String> getRoutes() {
        return new ArrayList<>(routesMap.keySet());
    }

    public Set<LuckVerifyEntity> getRouteEntityList() {
        return new HashSet<>(routesMap.values());
    }

    /**
     * 从注解数组中找到关于请求的注解，并且把route取出来，如果有存ROOT则会被route的加上前缀
     * @param annotations
     * @param root
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<LuckVerifyEntity> getAllPath(Annotation[] annotations, String root) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<LuckVerifyEntity> result = new LinkedList<>();
        String finalRoot = (root==null?"/":root.charAt(0) == '/'?root:root+"/");
        for (Annotation annotation : annotations) {
            RequestMapping action = null;
            if (annotation.annotationType() == RequestMapping.class || (action = annotation.annotationType().getAnnotation(RequestMapping.class)) != null){
                Method methodValue = annotation.annotationType().getMethod("value");
                Set<RequestMethod> routeMethods;
                if (action == null) {
                    action = (RequestMapping) annotation;
                }
                Method methodMethod = action.annotationType().getMethod("method");
                routeMethods = Arrays.stream((RequestMethod[]) methodMethod.invoke(action)).collect(Collectors.toSet());
                if (routeMethods.size() == 0) routeMethods = Arrays.stream(RequestMethod.values()).collect(Collectors.toSet());
                List<String> paths = Arrays.stream((String[]) methodValue.invoke(annotation)).collect(Collectors.toList());
                if (paths.size() == 0) {
                    paths.add(finalRoot);
                }else {
                    paths = paths.stream().map(v-> {
                        if (v.charAt(0) == '/' && finalRoot.equals("/")) return v;
                        else return finalRoot + v;
                    }).collect(Collectors.toList());
                }
                for (String path : paths) {
                    for (RequestMethod routeMethod : routeMethods) {
                        LuckVerifyEntity loginVerify = new LuckVerifyEntity(path.replaceAll("\\{(?<=\\{)[^}]*(?=\\})\\}", "*"), routeMethod);
                        result.add(loginVerify);
                    }
                }
                break;
            }
        }
        return result;
    }


    private void getAnnotationRoute(LuckVerifyEntity route, LuckVerify luckVerify, boolean isChild) {
        LuckVerifyEntity mapRoute = routesMap.get(route.getMethodRoute());
        if (mapRoute == null) mapRoute = route;
        if (luckVerify.ignore()) mapRoute.setIgnore();
        mapRoute.setDescription(luckVerify.value());
        routesMap.put(route.getMethodRoute(), mapRoute);
        routePaths.add(route.getRoute());
        if (!isChild && luckVerify.children()) {
            String parentRoute = route.getMethodRoute() + "/**";
            LuckVerifyEntity childMapRoute = routesMap.get(parentRoute);
            if (childMapRoute == null) childMapRoute = new LuckVerifyEntity(route.getRoute()+"/**", route.getRequestMethod());
            if (luckVerify.ignore()) childMapRoute.setIgnore();
            childMapRoute.setDescription(luckVerify.value());
            routesMap.put(parentRoute, childMapRoute);
            routePaths.add(childMapRoute.getRoute());
        }
    }



    public void addPackVerifyAnnotation(String pack) throws Exception {
        Set<Class<?>> classes = ClassUtils.getClasses(pack);
        for (Class<?> aClass : classes) {
            LuckVerify luckVerify = aClass.getAnnotation(LuckVerify.class);
            List<LuckVerifyEntity> classPaths = getAllPath(aClass.getAnnotations(),null);
            if (!(luckVerify == null)) {
                if (!classPaths.isEmpty()) {
                    classPaths.forEach(path -> {
                        getAnnotationRoute(path, luckVerify,false);
                    });
                }
            }
            String root = "";
            if (!classPaths.isEmpty()) root = classPaths.get(0).getRoute();
            for (Method method : aClass.getMethods()) {
                LuckVerify methodLuckVerify = method.getAnnotation(LuckVerify.class);
                if(methodLuckVerify != null) {
                    List<LuckVerifyEntity> methodPaths = getAllPath(method.getAnnotations(), root);
                    if (!methodPaths.isEmpty()) {
                        for (LuckVerifyEntity path : methodPaths) {
                            getAnnotationRoute(path, methodLuckVerify,true);
                        }
                    } else if (!classPaths.isEmpty()){
                        getAnnotationRoute(classPaths.get(0), methodLuckVerify,true);
                    }
                }
            }
        }
    }


    public LuckVerifyEntity getCurrentLoginVerify(String method, String route) {
        LuckVerifyEntity luckVerifyEntity = routesMap.get(method+":"+route);
        if (luckVerifyEntity == null){
            String[] split = route.split("/");
            if (split.length==0) return null;
            String newRoute = split[0];
            for (int i=1; i<split.length; i++) {
                newRoute+="/" + split[i];
                luckVerifyEntity = routesMap.get(method+":"+newRoute + "/**");
                if (luckVerifyEntity != null) return luckVerifyEntity;
            }
            if (luckVerifyEntity == null) return null;
        }
        if(luckVerifyEntity.checkMethod(method) && luckVerifyEntity.checkRoute(route))return luckVerifyEntity;
        return null;
    }

    public String routePathConver(Map pathVariables, String route) {
        if (pathVariables.values().size()>0) {
            Object[] objects = pathVariables.values().toArray();
            route = route.replace("/" + objects[0], "/*");
            for (Object value : objects) {
                route = route.replace("/" + value.toString() + "/", "/*/");
            }
        }
        return route;
    }
}
