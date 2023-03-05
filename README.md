# LuckPermission

#### 介绍
简单的springboot权限校验
您只需要引入此包，然后配置文件中指定controller包名，并且在Mapping注解上使用@LuckVerify注解即可，它会根据您当前Mapping的method进行权限比较。

#### 软件架构
springboot、反射


#### 安装教程
在项目application配置中加入以下内容
```yaml
luck-permission:
# packages 是包名，可扫数组
  packages: com.hbucvc.demo.controller
# verify-interceptors 是拦截器，可多个拦截器，若不指定则默认进入LuckPermission提供的默认DefaultVerifyInterceptor拦截器
  verify-interceptors: com.hbucvc.demo.config.interceptors.MyLucVertify
```

#### 使用说明

如果要自己定义一个拦截器很简单！
只需要实现VerifyInterceptor接口，并且在配置项中的luck-permission.verify-interceptors内添加类路径即可！
