package cn.zhz.privacy.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZHZ
 * @since 2023-03-07
 */
public class CacheUtil {

    private static final Map<Class<?>, List<Field>> FIELDS__Map = new HashMap<>();

    /**
     * 获取字段列表
     *
     * @param oClass
     * @return
     */
    public static List<Field> getFields(Class<?> oClass) {
        List<Field> fields = FIELDS__Map.get(oClass);
        if (fields == null || fields.size() == 0) {
            fields = mergeField(oClass, null);
            FIELDS__Map.put(oClass, fields);
        }
        return fields;
    }


    /**
     * 聚合父类属性
     *
     * @param oClass
     * @param fields
     * @return
     */
    private static List<Field> mergeField(Class<?> oClass, List<Field> fields) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        Class<?> superclass = oClass.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) && superclass.getDeclaredFields().length > 0) {
            mergeField(superclass, fields);
        }
        for (Field declaredField : oClass.getDeclaredFields()) {

            int modifiers = declaredField.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isSynchronized(modifiers)) {
                continue;
            }
            fields.add(declaredField);
        }

        return fields;

    }
}
