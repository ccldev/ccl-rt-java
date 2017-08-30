package coa.rt;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.data.Func;

/**
 * Created by Matthias on 14.06.2017.
 */
public class Nvp extends Expression{
    private final Func<Void, NvpVal> value;

    public Nvp(IVM vm, Func<Void, NvpVal> value) {
        super(vm, value);
        this.value = value;
    }

    public Value invoke(Value... args) throws Exception {
        NvpVal v = value.call(null);
        String n = v.getName().getValue().toString();

        args[0].getProperty(false,n).setValue(v.getValue());
        return args[0].getProperty(false,n);
    }

    public static Value makeNvp(IVM vm, NvpVal nvpVal) {
        return new Nvp(vm, new Func<Void, NvpVal>() {
            @Override
            public NvpVal call(Void aVoid) {
                return nvpVal;
            }
        });
    }

    public static String makeMethodName(String prefix, String name){
        return prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
