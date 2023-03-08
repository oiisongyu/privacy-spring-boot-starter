package cn.zhz.privacy.utils;

import java.lang.reflect.Field;
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
            fields = FieldUtil.mergeField(oClass, new ArrayList<>());
            FIELDS__Map.put(oClass, fields);
        }
        return fields;
    }

}
