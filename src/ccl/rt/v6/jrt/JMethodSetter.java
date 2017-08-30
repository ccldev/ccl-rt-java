package ccl.rt.v6.jrt;

import ccl.jrt.J;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.reflect.SpecificMethods;

public class JMethodSetter implements Func<Value, Void> {

    private final IVM vm;
    private final Object instance;
    private final SpecificMethods methods;

    public JMethodSetter(IVM vm, Object instance, SpecificMethods methods) {
        this.vm = vm;
        this.instance = instance;
        this.methods = methods;
    }

    @Override
    public Void call(Value value) {
        J.invoke(vm, instance, methods, new Value[]{value});
        return null;
    }

}
