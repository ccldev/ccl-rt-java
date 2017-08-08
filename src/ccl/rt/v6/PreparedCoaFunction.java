package ccl.rt.v6;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Value;
import ccl.rt.store.Scope;
import ccl.rt.vm.CclVm;
import ccl.rt.vm.IVM;
import ccl.rt.vm.Runner;
import ccl.rt.vm.StackTraceFormer;
import cpa.subos.io.IOBase;

import java.util.ArrayList;

public class PreparedCoaFunction {

    private final Runner runner;
    private final IVM vm;

    public PreparedCoaFunction(CclVm vm, IOBase<?> code, Runner r){
        r.creation(code);
        this.runner = r;
        this.vm = vm;
    }

    public Value invoke(Scope funcScope){
        Value v;
        try {
            v = runner.execute(vm, funcScope);
        } catch (Exception e) {
            e.printStackTrace();
            Exception ex = StackTraceFormer.formException(e, vm);
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return v;
    }

}
