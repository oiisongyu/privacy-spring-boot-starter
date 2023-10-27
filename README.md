# privacy-spring-boot-starter

#### 介绍
基于mybatis对隐私数据处理如加密、解密、脱敏等
springboot 2.0
mybatis 3.5

#### 软件架构
软件架构说明


#### 安装教程


#### 使用说明

1.  添加依赖

```
        <dependency>
            <groupId>com.gitee.china-zhz</groupId>
            <artifactId>privacy-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>
```


```
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
```


2.  @FieldEncrypt注解放到要加密解密类的属性上就可以了如：

```
    @FieldEncrypt
    private String password;

```

3.  @FieldEncrypt 注解默认使用AES算法，现在是集成了两种算法MD5和AES ，MD5是不可逆算法不可以解密，AES可以反向解密，默认的AES加密解密时我固定了一个秘钥，如果想自定义秘钥有两种方式：

① 全局配置yml文件

```
privacy:
  crypto:
    key: jshfdiwhfkjncwolmas
```

② 加注解上

```
    @FieldEncrypt(key = "qwertyuiop")
    private String password;
```
 **注解秘钥优先级高于全局秘钥** 

4.  如果你想使用MD5加密

```
    @FieldEncrypt(algorithm = Algorithm.MD5)
    private String password;
```
5.  如果这两个都不想用可以自定义加密解密器只需要实现ICrypto接口自定义加密解密方法并注入到spring容器即可

```
@Slf4j
@Component
public class MyCrypto implements ICrypto {

    @Override
    public String encrypt(Algorithm algorithm, String s, String s1) throws Exception {
        log.debug("---------------------------"+s+s1);
        return "zxcvbnm";
    }

    @Override
    public String decrypt(Algorithm algorithm, String s, String s1) throws Exception {
        log.debug("---------------------------"+s+s1);
        return "mnbvcxz";
    }
}
```
6.  @FieldDesensitize加类属性上可实现字段脱敏

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
