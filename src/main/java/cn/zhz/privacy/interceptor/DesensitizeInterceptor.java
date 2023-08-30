package cn.zhz.privacy.interceptor;


import cn.zhz.privacy.annotation.FieldDesensitize;
import cn.zhz.privacy.desensitizer.IDesensitizer;
import cn.zhz.privacy.handler.DesensitizeAnnotationHandler;
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
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author ZHZ
 * @date 2021-11-15
 * @apiNote
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
@Slf4j
public class DesensitizeInterceptor implements Interceptor, ApplicationContextAware {


    private ApplicationContext applicationContext;

    private final DesensitizeAnnotationHandler desensitizeAnnotationCacheHandler;

    public DesensitizeInterceptor(DesensitizeAnnotationHandler desensitizeAnnotationCacheHandler) {
        this.desensitizeAnnotationCacheHandler = desensitizeAnnotationCacheHandler;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        return selectHandle(invocation);

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

        resultList.forEach(object -> {
            if (object != null) {
                desensitizeAnnotationCacheHandler.parse(object.getClass());
                handleObject(object, object.getClass());
            }
        });
        return resultList;
    }


    /**
     * 处理Object
     *
     * @param obj
     * @param oClass
     */
    private void handleObject(Object obj, Class<?> oClass) {

        Set<Field> fields = desensitizeAnnotationCacheHandler.getFields(oClass);

        if (fields == null || fields.isEmpty()) {
            return;
        }

        for (Field declaredField : fields) {

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

                FieldDesensitize annotation = declaredField.getAnnotation(FieldDesensitize.class);
                if (annotation != null) {
                    try {
                        setValue(declaredField, obj, annotation);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

            } else if (value instanceof Collection) {
                for (Object o : (Collection) value) {
                    handleObject(o, o.getClass());
                }
            } else {
                handleObject(value, value.getClass());
            }
        }

    }

    /**
     * 处理字符
     *
     * @param field  字段
     * @param object 字段原值
     * @throws IllegalAccessException
     */
    private void setValue(Field field, Object object, FieldDesensitize annotation) throws IllegalAccessException {

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(object);

        if (annotation != null) {

            String fillValue = annotation.fillValue();
            IDesensitizer iDesensitizer = applicationContext.getBean(annotation.desensitizer());
            String desensitizeValue = iDesensitizer.execute(String.valueOf(value), fillValue);

            log.debug("原值：" + value);
            log.debug("脱敏后：" + desensitizeValue);
            field.set(object, String.valueOf(desensitizeValue));
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
