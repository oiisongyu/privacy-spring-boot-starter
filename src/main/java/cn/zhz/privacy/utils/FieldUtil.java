package cn.zhz.privacy.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ZHZ
 * @since 2023-03-08
 */
public class FieldUtil {

    /**
     * 聚合父类属性
     *
     * @param oClass
     * @param fields
     * @return
     */
    protected static List<Field> mergeField(Class<?> oClass, List<Field> fields) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        Class<?> superclass = oClass.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) && superclass.getDeclaredFields().length > 0) {
            mergeField(superclass, fields);
        }

        for (Field declaredField : oClass.getDeclaredFields()) {

            // 过滤类型
            Class<?> declaredFieldType = declaredField.getType();
            if (declaredFieldType.equals(Long.class) ||
                    declaredFieldType.equals(Integer.class) ||
                    declaredFieldType.equals(LocalDateTime.class) ||
                    declaredFieldType.equals(LocalDate.class) ||
                    declaredFieldType.equals(Date.class) ||
                    declaredFieldType.equals(Year.class)
            ) {
                continue;
            }
            int modifiers = declaredField.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isSynchronized(modifiers)) {
                continue;
            }

            fields.add(declaredField);

        }

        return fields;

    }


    /**
     * 是否是基本类型
     *
     * @param type
     * @return
     */
    private boolean isBase(Type type) {

        return boolean.class.equals(type) ||
                char.class.equals(type) ||
                long.class.equals(type) ||
                int.class.equals(type) ||
                byte.class.equals(type) ||
                short.class.equals(type) ||
                double.class.equals(type) ||
                float.class.equals(type);
    }
}
