package coa.rt;

import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;

import java.util.List;

/**
 * Created by Matthias on 14.06.2017.
 */
public class NvpVal {

    private final Value name;
    private final Value value;

    public NvpVal(Value name, Value value) {
        this.name = name;
        this.value = value;
    }

    public Value getName() {
        return name;
    }

    public Value getValue() {
        return value;
    }
}
