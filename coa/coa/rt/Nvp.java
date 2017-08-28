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

        return args[0].getProperty(false,"set" + Character.toUpperCase(n.charAt(0)) + n.substring(1)).invoke(v.getValue());
    }

    public static Value makeNvp(IVM vm, NvpVal nvpVal) {
        return new Nvp(vm, new Func<Void, NvpVal>() {
            @Override
            public NvpVal call(Void aVoid) {
                return nvpVal;
            }
        });
    }
}
