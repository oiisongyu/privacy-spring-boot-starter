package cn.zhz.privacy.handler;

import cn.zhz.privacy.annotation.FieldEncrypt;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ZHZ
 * @since 2023-03-07
 */
public class CryptAnnotationHandler extends AbstractAnnotationHandler<FieldEncrypt> {


    private static final Map<Class<?>, Set<Field>> FIELDS__Map = new ConcurrentHashMap<>();

    private static final Set<Class<?>> CLASS_SET = new CopyOnWriteArraySet<>();

    public CryptAnnotationHandler() {
        super(FieldEncrypt.class);
    }

    protected Map<Class<?>, Set<Field>> getFieldsMap() {
        return FIELDS__Map;
    }

    protected Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }


}
