package cn.zhz.privacy.handler;

import cn.zhz.privacy.annotation.FieldDesensitize;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ZHZ
 * @since 2023-03-07
 */
public class DesensitizeAnnotationHandler extends AbstractAnnotationHandler<FieldDesensitize> {

    private static final Map<Class<?>, Set<Field>> FIELDS__Map = new ConcurrentHashMap<>();
    private static final Set<Class<?>> CLASS_SET = new HashSet<>();
    private static final Set<Class<?>> HANDLING_CLASS_SET = new CopyOnWriteArraySet<>();

    public DesensitizeAnnotationHandler() {
        super(FieldDesensitize.class);
    }


    protected Map<Class<?>, Set<Field>> getFieldsMap() {
        return FIELDS__Map;
    }

    protected Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    @Override
    Set<Class<?>> getHandlingClassSet() {
        return HANDLING_CLASS_SET;
    }
}
