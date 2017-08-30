package ccl.jrt;

import ccl.jrt.JCast;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.reflect.Clss;

import java.lang.reflect.Field;

public class JFieldSetter implements Func<Value, Void> {

    private final Field field;
    private final Object instance;
    private final IVM vm;

    public JFieldSetter(IVM vm, Object instance, Field field) {
        this.vm = vm;
        this.field = field;
        this.instance = instance;
    }

    @Override
    public Void call(Value n) {
        try {
            Object o = JCast.cast(vm,instance,new Value[]{n}, new Clss[]{new Clss(field.getType())})[0];
            field.set(instance,o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
