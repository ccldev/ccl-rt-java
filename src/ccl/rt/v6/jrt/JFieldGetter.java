package ccl.rt.v6.jrt;

import ccl.rt.Value;
import io.github.coalangsoft.lib.data.Func;

import java.lang.reflect.Field;

public class JFieldGetter implements Func<Void, Object> {

    private final Object instance;
    private final Field field;

    public JFieldGetter(Object instance, Field field) {
        this.instance = instance;
        this.field = field;
    }

    @Override
    public Object call(Void aVoid) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
