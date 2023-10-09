package cn.zhz.privacy.interceptor;


import cn.zhz.privacy.annotation.FieldEncrypt;
import cn.zhz.privacy.crypto.ICrypto;
import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.enums.CryptoType;
import cn.zhz.privacy.handler.CryptAnnotationHandler;
import cn.zhz.privacy.properties.CryptoProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

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
@Slf4j
public class CryptoInterceptor implements Interceptor, ApplicationContextAware {


    private final CryptoProperties cryptoProperties;
    private ApplicationContext applicationContext;

    private final CryptAnnotationHandler cryptAnnotationCacheHandler;

    public CryptoInterceptor(CryptoProperties cryptoProperties, CryptAnnotationHandler cryptAnnotationCacheHandler) {
        this.cryptoProperties = cryptoProperties;
        this.cryptAnnotationCacheHandler = cryptAnnotationCacheHandler;
    }

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

    /**
     * 查询操作处理
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    private Object selectHandle(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler<Object> resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();

        CacheKey cacheKey;
        BoundSql boundSql;

        //处理参数作为条件查询需要加密
        handleParameterOrResult(parameter, CryptoType.ENCRYPT);

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

        List<Object> resultList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);

        resultList.forEach(result -> handleParameterOrResult(result, CryptoType.DECRYPT));

        return resultList;
    }

    /**
     * 新增修改操作处理
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    private Object updateHandle(Invocation invocation) throws Throwable {
        //处理参数
        handleParameterOrResult(invocation.getArgs()[1], CryptoType.ENCRYPT);
        return invocation.proceed();
    }

    /**
     * 处理参数或结果
     *
     * @param object
     * @param cryptoType
     */
    private void handleParameterOrResult(Object object, CryptoType cryptoType) {
        //多个参数
        if (object instanceof Map) {
            Map paramMap = (Map) object;
            Set keySet = paramMap.keySet();
            // 处理过的对象记录
            Set<Object> handleObjectSet = new HashSet<>();
            for (Object key : keySet) {
                Object o = paramMap.get(key);
                // 如果参数是集合类型，根据遍历处理
                if (o instanceof Collection) {
                    for (Object item : ((Collection<?>) o)) {
                        if (handleObjectSet.contains(item)) {
                            continue;
                        }
                        cryptAnnotationCacheHandler.parse(item.getClass());
                        handleObject(item, item.getClass(), cryptoType);
                        handleObjectSet.add(item);
                    }
                }
                if (o != null) {
                    //
                    cryptAnnotationCacheHandler.parse(o.getClass());
                    handleObject(o, o.getClass(), cryptoType);
                }

            }
        } else {
            if (object != null) {
                //
                cryptAnnotationCacheHandler.parse(object.getClass());
                handleObject(object, object.getClass(), cryptoType);
            }
        }

    }


    /**
     * 处理Object
     *
     * @param obj
     * @param oClass
     */
    private void handleObject(Object obj, Class<?> oClass, CryptoType cryptoType) {


        Set<Field> fields = cryptAnnotationCacheHandler.getFields(oClass);
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (Field declaredField : fields) {

            //静态属性直接跳过
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }

            boolean accessible = declaredField.isAccessible();
            declaredField.setAccessible(true);
            Object value;
            try {
                value = declaredField.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            declaredField.setAccessible(accessible);

            if (value == null || value instanceof Number) {

            } else if (value instanceof CharSequence) {

                FieldEncrypt annotation = declaredField.getAnnotation(FieldEncrypt.class);
                if (annotation != null) {
                    try {
                        setValue(declaredField, obj, cryptoType, annotation);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

            } else if (value instanceof Collection) {
                Collection coll = (Collection) value;
                for (Object o : coll) {
                    handleObject(o, o.getClass(), cryptoType);
                }
            } else {
                handleObject(value, value.getClass(), cryptoType);
            }
        }

    }

    /**
     * 处理值
     *
     * @param field
     * @param object
     * @param cryptoType
     */
    private void setValue(Field field, Object object, CryptoType cryptoType, FieldEncrypt annotation) throws Exception {

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(object);

        if (annotation != null) {

            String key;
            //全局配置的key
            String propertiesKey = cryptoProperties.getKey();
            log.debug("全局key是：" + propertiesKey);
            //属性上的key
            String annotationKey = annotation.key();
            log.debug("注解key是：" + annotationKey);

            if (!"".equals(annotationKey)) {
                key = annotationKey;
            } else {
                key = propertiesKey;
            }

            Algorithm algorithm = annotation.algorithm();

            ICrypto iCrypto = applicationContext.getBean(annotation.iCrypto());

            String valueResult;

            if (cryptoType.equals(CryptoType.DECRYPT)) {
                valueResult = iCrypto.decrypt(algorithm, String.valueOf(value), key);
            } else {
                valueResult = iCrypto.encrypt(algorithm, String.valueOf(value), key);
            }

            log.debug("原值：" + value);
            log.debug("现在：" + valueResult);
            field.set(object, String.valueOf(valueResult));
            field.setAccessible(accessible);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
