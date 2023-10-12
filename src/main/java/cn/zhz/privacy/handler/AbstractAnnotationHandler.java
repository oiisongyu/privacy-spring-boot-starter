package cn.zhz.privacy.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public abstract class AbstractAnnotationHandler<T extends Annotation> {

    private final Class<T> annotationClass;

    public AbstractAnnotationHandler(Class<T> annotationClass) {
        this.annotationClass = annotationClass;
    }

    abstract Map<Class<?>, Set<Field>> getFieldsMap();

    abstract Set<Class<?>> getClassSet();

    /**
     * 获取正在处理中的类
     *
     * @return
     */
    abstract Set<Class<?>> getHandlingClassSet();

    /**
     * 是否解析过该class
     *
     * @param oClass
     * @return
     */
    public boolean isHandleClass(Class<?> oClass) {
        return getClassSet().contains(oClass);
    }

    /**
     * 是否解析中class
     *
     * @param oClass
     * @return
     */
    public boolean isHandlingClass(Class<?> oClass) {
        return getHandlingClassSet().contains(oClass);
    }

    /**
     * 将该class标识为解析过
     *
     * @param oClass
     */
    public void addHandleClass(Class<?> oClass) {
        getClassSet().add(oClass);
    }

    /**
     * 将该class标识为解析中
     *
     * @param oClass
     */
    public void addHandlingClass(Class<?> oClass) {
        getHandlingClassSet().add(oClass);
    }

    /**
     * 去除该class解析中标识
     *
     * @param oClass
     */
    public void removeHandlingClass(Class<?> oClass) {
        getHandlingClassSet().remove(oClass);
    }

    /**
     * 获取字段列表
     *
     * @param oClass
     * @return
     */
    public Set<Field> getFields(Class<?> oClass) {
        // 如果正在处理中循环等待
        while (isHandlingClass(oClass)) {
        }
        return getFieldsMap().get(oClass);
    }

    /**
     * 添加字段缓存
     *
     * @param oClass
     * @param field
     */
    public void addField(Class<?> oClass, Field field) {
        getFieldsMap().computeIfAbsent(oClass, k -> new HashSet<>());
        getFields(oClass).add(field);
    }

    /**
     * 聚合父类属性
     *
     * @param oClass
     * @return
     */
    public boolean parse(Class<?> oClass) {


        // 已处理的类无需再次处理
        if (isHandleClass(oClass)) {
            Set<Field> fields = getFields(oClass);
            return fields != null && !fields.isEmpty();
        }
        if (isFilter(oClass)) {
            return false;
        }
        // 标记为正在处理中
        addHandlingClass(oClass);

        boolean haveAnnotationField = false;
        Class<?> superclass = oClass.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) && superclass.getDeclaredFields().length > 0) {
            parse(superclass);
        }

        for (Field declaredField : oClass.getDeclaredFields()) {

            int modifiers = declaredField.getModifiers();

            // 过滤类型
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isSynchronized(modifiers)) {
                continue;
            }

            // 过滤class
            Class<?> declaredFieldType = declaredField.getType();
            if (isFilter(declaredFieldType)) {
                continue;
            }
            // 如果字符类型
            if (CharSequence.class.isAssignableFrom(declaredFieldType)) {
                T annotation = declaredField.getAnnotation(this.annotationClass);
                if (annotation != null) {
                    haveAnnotationField = true;
                    addField(oClass, declaredField);
                }
            // 如果该字段为数组类型
            } else if (Collection.class.isAssignableFrom(declaredFieldType)) {

                Type genericType = declaredField.getGenericType();
                // 判断是否是正在处理中的类
                if (isHandlingClass(genericType.getClass())) {
                    continue;
                }
                boolean childHaveAnnotationField = parse(genericType.getClass());
                if (childHaveAnnotationField) {
                    haveAnnotationField = true;
                    addField(oClass, declaredField);
                }
                // 如果该字段为对象类型
            } else if (Object.class.isAssignableFrom(declaredFieldType)) {
                // 判断是否是正在处理中的类
                if (isHandlingClass(declaredFieldType)) {
                    continue;
                }
                boolean childHaveAnnotationField = parse(declaredFieldType);
                if (childHaveAnnotationField) {
                    haveAnnotationField = true;
                    addField(oClass, declaredField);
                }
            }


        }
        // 标记为已处理
        addHandleClass(oClass);
        // 去除处理中标识
        removeHandlingClass(oClass);
        return haveAnnotationField;
    }


    /**
     * 是否是
     *
     * @param clazz
     * @return
     */
    private boolean isFilter(Class<?> clazz) {
        return
                Number.class.isAssignableFrom(clazz) ||
                        Long.class.isAssignableFrom(clazz) ||
                        TemporalAccessor.class.isAssignableFrom(clazz) ||
                        Date.class.isAssignableFrom(clazz)
                ;
    }

}
