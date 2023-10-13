package cn.zhz.privacy.handler;

import cn.zhz.privacy.annotation.FieldDesensitize;
import cn.zhz.privacy.desensitizer.IDesensitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author z.h.z
 * @since 2023/10/13
 */
@Slf4j
@RequiredArgsConstructor
public class DesensitizeHandler implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final DesensitizeAnnotationHandler desensitizeAnnotationCacheHandler;


    /**
     * 处理结果集
     *
     * @param resultList
     */
    public void handleResultList(List<Object> resultList) {
        resultList.forEach(object -> {
            if (object != null) {
                desensitizeAnnotationCacheHandler.parse(object.getClass());
                handleObject(object, object.getClass());
            }
        });
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
