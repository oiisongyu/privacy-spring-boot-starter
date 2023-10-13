package cn.zhz.privacy.handler;

import cn.zhz.privacy.enums.SerializeType;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@Slf4j
public abstract class AbstractHandler<T extends Annotation> {


    private final Class<T> annotationClass;

    public AbstractHandler(Class<T> annotationClass) {
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
                Class<?> actualTypeArgumentClass;
                try {
                    actualTypeArgumentClass = Class.forName(((ParameterizedTypeImpl) declaredField.getGenericType()).getActualTypeArguments()[0].getTypeName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                boolean childHaveAnnotationField;
                // 判断是否是正在处理中的类
                if (isHandlingClass(actualTypeArgumentClass)) {
                    childHaveAnnotationField = true;
                } else {
                    childHaveAnnotationField = parse(actualTypeArgumentClass);
                }
                if (childHaveAnnotationField) {
                    haveAnnotationField = true;
                    addField(oClass, declaredField);
                }
                // 如果该字段为对象类型
            } else if (Object.class.isAssignableFrom(declaredFieldType)) {
                // 判断是否是正在处理中的类
                boolean childHaveAnnotationField;
                if (isHandlingClass(declaredFieldType)) {
                    childHaveAnnotationField = true;
                } else {
                    childHaveAnnotationField = parse(declaredFieldType);
                }
                if (childHaveAnnotationField) {
                    haveAnnotationField = true;
                    addField(oClass, declaredField);
                }
            }


        }
        // 标记为已处理
        addHandleClass(oClass);
//        // 去除处理中标识
//        removeHandlingClass(oClass);
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

    abstract String getAfterValue(T annotation, String originalValue, SerializeType serializeType);

    /**
     * 处理Object
     *
     * @param obj
     * @param oClass
     */
    protected void handleObject(Object obj, Class<?> oClass, SerializeType serializeType) {


        Set<Field> fields = getFields(oClass);
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
                T annotation = declaredField.getAnnotation(annotationClass);
                if (annotation != null) {
                    try {
                        setValue(declaredField, obj, annotation, serializeType);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            } else if (value instanceof Collection) {
                Collection coll = (Collection) value;
                for (Object o : coll) {
                    handleObject(o, o.getClass(), serializeType);
                }
            } else {
                handleObject(value, value.getClass(), serializeType);
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
    public void setValue(Field field, Object object, T annotation, SerializeType serializeType) throws IllegalAccessException {

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        String value = (String) field.get(object);
        if (annotation != null) {
            String afterValue = getAfterValue(annotation, value, serializeType);
            log.debug("原值：" + value);
            log.debug("处理后：" + afterValue);
            field.set(object, afterValue);
            field.setAccessible(accessible);
        }
    }


}
