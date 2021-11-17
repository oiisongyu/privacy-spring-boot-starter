package cn.zhz.privacy.interceptor;


import cn.zhz.privacy.annotation.FieldEncrypt;
import cn.zhz.privacy.crypto.ICrypto;
import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.properties.CryptoProperties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * @author ZHZ
 * @date 2021-11-15
 * @apiNote
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
@Component
public class CryptoInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(CryptoInterceptor.class.getName());// slf4j日志记录器

    @Autowired
    private CryptoProperties cryptoProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        switch (method.getName()) {
            case "update":
                return updateHandle(invocation);
            case "query":
                return selectHandle(invocation);
            default:
                return invocation.proceed();
        }

    }

    private Object selectHandle(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler<Object> resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();

        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        List<Object> resultList;
        resultList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        for (Object o : resultList) {

            Class<?> aClass = o.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                FieldEncrypt annotation = declaredField.getAnnotation(FieldEncrypt.class);
                if (annotation != null) {

                    // 获得属性描述器 如属性在父类：object.getClass().getSuperclass()
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(declaredField.getName(), aClass);
                    // 获得get方法
                    Method getMethod = propertyDescriptor.getReadMethod();
                    // 调用指定对象get方法
                    Object value = getMethod.invoke(o);
                    if (declaredField.getType() == String.class) {
                        //值为空跳过加密
                        if (value == null) {
                            continue;
                        }
                        String key = null;
                        //全局配置的key
                        String propertiesKey = cryptoProperties.getKey();
                        log.debug("全局key是：" + propertiesKey);
                        //属性上的key
                        String annotationKey = annotation.key();
                        log.debug("注解key是：" + annotationKey);

                        if (propertiesKey != null && !"".equals(propertiesKey)) {
                            key = annotationKey;
                        }

                        Algorithm algorithm = annotation.algorithm();
                        Class<? extends ICrypto> iCryptoImpl = annotation.iCrypto();
                        ICrypto iCrypto = iCryptoImpl.newInstance();
                        Method decrypt = iCryptoImpl.getDeclaredMethod("decrypt", Algorithm.class, String.class, String.class);
                        //解密后的值
                        Object valueDecrypt = decrypt.invoke(iCrypto, algorithm, String.valueOf(value), key);
                        // 获得set方法
                        Method setMethod = propertyDescriptor.getWriteMethod();
                        // 调用指定对象set方法
                        setMethod.invoke(o, String.valueOf(valueDecrypt));
                    } else {
                        throw new RuntimeException("FieldEncrypt 注解只能加在值非空的String类型上");
                    }
                }

            }
        }

        return resultList;
    }

    private Object updateHandle(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        for (Object arg : args) {

            if (arg instanceof MappedStatement) {
                continue;
            }
            Class<?> aClass = arg.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                FieldEncrypt annotation = declaredField.getAnnotation(FieldEncrypt.class);
                if (annotation != null) {

                    // 获得属性描述器 如属性在父类：object.getClass().getSuperclass()
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(declaredField.getName(), aClass);
                    // 获得get方法
                    Method getMethod = propertyDescriptor.getReadMethod();
                    // 调用指定对象get方法
                    Object value = getMethod.invoke(arg);
                    if (declaredField.getType() == String.class) {
                        //值为空跳过解密
                        if (value == null) {
                            continue;
                        }
                        //属性上的key
                        String key = annotation.key();
                        Algorithm algorithm = annotation.algorithm();
                        Class<? extends ICrypto> iCryptoImpl = annotation.iCrypto();
                        ICrypto iCrypto = iCryptoImpl.newInstance();
                        Method encrypt = iCryptoImpl.getDeclaredMethod("encrypt", Algorithm.class, String.class, String.class);
                        //加密后的值
                        Object valueEncrypt = encrypt.invoke(iCrypto, algorithm, String.valueOf(value), key);
                        // 获得set方法
                        Method setMethod = propertyDescriptor.getWriteMethod();
                        // 调用指定对象set方法
                        setMethod.invoke(arg, String.valueOf(valueEncrypt));

                    } else {
                        throw new RuntimeException("FieldEncrypt 注解只能加在String属性上");
                    }
                }

            }

        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
