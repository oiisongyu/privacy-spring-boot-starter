package cn.zhz.privacy.handler;

import cn.zhz.privacy.annotation.FieldEncrypt;
import cn.zhz.privacy.crypto.ICrypto;
import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.enums.CryptoType;
import cn.zhz.privacy.properties.CryptoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author z.h.z
 * @since 2023/10/13
 */
@Slf4j
@RequiredArgsConstructor
public class CryptHandler implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final CryptoProperties cryptoProperties;

    private final CryptAnnotationHandler cryptAnnotationHandler;


    /**
     * 处理结果集
     *
     * @param resultList
     */
    public void handleResultList(List<Object> resultList) {
        resultList.forEach(object -> {
            if (object != null) {
                cryptAnnotationHandler.parse(object.getClass());
                handleParameterOrResult(object, CryptoType.DECRYPT);
            }
        });
    }

    /**
     * 处理参数
     *
     * @param parameter
     */
    public void handleParam(Object parameter) {
        handleParameterOrResult(parameter, CryptoType.ENCRYPT);
    }

    /**
     * 处理参数或结果
     *
     * @param object
     * @param cryptoType
     */
    protected void handleParameterOrResult(Object object, CryptoType cryptoType) {
        //多个参数
        if (object instanceof Map) {
            Map paramMap = (Map) object;
            Set keySet = paramMap.keySet();
            // 处理过的对象记录
            Set<Object> handleObjectSet = new HashSet<>();
            for (Object key : keySet) {
                Object o = paramMap.get(key);
                // 如果参数是集合类型，根据遍历处理

                if (o != null) {
                    if (o instanceof Collection) {
                        for (Object item : ((Collection<?>) o)) {
                            if (handleObjectSet.contains(item)) {
                                continue;
                            }
                            cryptAnnotationHandler.parse(item.getClass());
                            handleObject(item, item.getClass(), cryptoType);
                            handleObjectSet.add(item);
                        }
                    } else {
                        //
                        cryptAnnotationHandler.parse(o.getClass());
                        handleObject(o, o.getClass(), cryptoType);
                    }
                }
            }
        } else {
            if (object != null) {
                //
                cryptAnnotationHandler.parse(object.getClass());
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


        Set<Field> fields = cryptAnnotationHandler.getFields(oClass);
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
