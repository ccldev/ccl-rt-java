package ccl.rt.v6.jrt;

import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.reflect.SpecificMethods;

import java.util.Arrays;

public class JMethodGetter implements Func<Void, Object> {

    private final SpecificMethods methods;

    public JMethodGetter(SpecificMethods methods) {
        this.methods = methods;
    }

    @Override
    public Object call(Void aVoid) {
        return methods.call(new Object[0]);
    }

}
