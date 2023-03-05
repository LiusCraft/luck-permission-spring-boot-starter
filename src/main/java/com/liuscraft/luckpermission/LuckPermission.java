package com.liuscraft.luckpermission;

import com.liuscraft.luckpermission.annotations.LuckVerify;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.interceptors.VerifyInterceptor;
import com.liuscraft.luckpermission.utils.ClassUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
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
@ComponentScan("com.liuscraft.luckpermission")
public class LuckPermission {
    private static final Map<String, LuckVerifyEntity> routesMap = new HashMap<>();

    public static List<String> getRoutes() {
        return new ArrayList<>(routesMap.keySet());
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
    public static List<LuckVerifyEntity> getAllPath(Annotation[] annotations, String root) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<LuckVerifyEntity> result = new LinkedList<>();
        String finalRoot = root==null?"/":root+"/";
        for (Annotation annotation : annotations) {
            boolean isChild = false;
            if (annotation.annotationType() == RequestMapping.class || (isChild = annotation.annotationType().getAnnotation(RequestMapping.class) != null)){
                Method methodValue = annotation.annotationType().getMethod("value");
                Set<RequestMethod> routeMethods;
                if (!isChild) {
                    Method methodMethod = annotation.annotationType().getMethod("method");
                    routeMethods = Arrays.stream((RequestMethod[]) methodMethod.invoke(annotation)).collect(Collectors.toSet());
                }else routeMethods = Arrays.stream(annotation.annotationType().getAnnotation(RequestMapping.class).method()).collect(Collectors.toSet());
                if (routeMethods.size() == 0) routeMethods = Arrays.stream(RequestMethod.values()).collect(Collectors.toSet());
                List<String> paths = Arrays.stream((String[]) methodValue.invoke(annotation)).collect(Collectors.toList());
                paths = paths.stream().map(v-> finalRoot + v).collect(Collectors.toList());
                for (String path : paths) {
                    LuckVerifyEntity loginVerify = new LuckVerifyEntity(path.replaceAll("\\{(?<=\\{)[^}]*(?=\\})\\}", "*"), routeMethods);
                    result.add(loginVerify);
                }
            }
        }
        return result;
    }


    private static void getAnnotationRoute(LuckVerifyEntity route, LuckVerify luckVerify) {
        LuckVerifyEntity mapRoute = routesMap.get(route.getRoute());

        Set<RequestMethod> requestMethods = Arrays.stream(luckVerify.value()).collect(Collectors.toSet());
        if (mapRoute == null) mapRoute = route;
        mapRoute.addMethods(requestMethods);
        routesMap.put(route.getRoute(), mapRoute);
        if (luckVerify.children()) {
            String parentRoute = route.getRoute() + "/**";
            LuckVerifyEntity childMapRoute = routesMap.get(parentRoute);
            if (childMapRoute == null) childMapRoute = route;
            childMapRoute.addMethods(requestMethods);
            routesMap.put(parentRoute, childMapRoute);
        }
    }

    public static void addPackVerifyAnnotation(String pack) throws Exception {
        Set<Class<?>> classes = ClassUtils.getClasses(pack);
        for (Class<?> aClass : classes) {
            LuckVerify luckVerify = aClass.getAnnotation(LuckVerify.class);
            if (luckVerify == null || luckVerify.ignore()) continue;
            List<LuckVerifyEntity> classPaths = getAllPath(aClass.getAnnotations(),null);
            if (!classPaths.isEmpty()) {
                classPaths.forEach(path -> {
                    getAnnotationRoute(path, luckVerify);
                });
            }
            String root = "";
            if (!classPaths.isEmpty()) root = classPaths.get(0).getRoute();
            for (Method method : aClass.getMethods()) {
                LuckVerify methodLuckVerify = method.getAnnotation(LuckVerify.class);
                if(methodLuckVerify != null && !methodLuckVerify.ignore()) {
                    List<LuckVerifyEntity> methodPaths = getAllPath(method.getAnnotations(), root);
                    if (!methodPaths.isEmpty()) {
                        for (LuckVerifyEntity path : methodPaths) {
                            getAnnotationRoute(path, methodLuckVerify);
                        }
                    } else if (!classPaths.isEmpty()){
                        getAnnotationRoute(classPaths.get(0), methodLuckVerify);
                    }
                }
            }
        }
    }


    public static LuckVerifyEntity getCurrentLoginVerify(String method, String route) {
        LuckVerifyEntity luckVerifyEntity = routesMap.get(route);
        if (luckVerifyEntity == null){
            String[] split = route.split("/");
            if (split.length==0) return null;
            String newRoute = split[0];
            for (int i=1; i<split.length; i++) {
                newRoute+="/" + split[i];
                luckVerifyEntity = routesMap.get(newRoute + "/**");
                if (luckVerifyEntity != null) break;
            }
            if (luckVerifyEntity == null) return null;
        }
        if(luckVerifyEntity.checkMethod(method) && luckVerifyEntity.checkRoute(route))return luckVerifyEntity;
        return null;
    }

    public static String routePathConver(Map pathVariables, String route) {
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
