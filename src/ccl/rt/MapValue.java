package ccl.rt;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

import java.util.HashMap;

public class MapValue extends Expression {

    public MapValue(IVM vm){
        super(vm, (v) -> new HashMap<Object, Value>());
        setProperty("get", Func.by(vm, (v, a) -> {
            Value val = ((HashMap<Object, Value>) getValue()).get(a.getValue());
            if(val != null){
                return val;
            }else{
                return Expression.make(vm, Special.UNDEFINED);
            }
        }));
        setProperty("put", Func.by(vm, (v, a, b) -> {
            ((HashMap<Object, Value>) getValue()).put(a.getValue(),b);
            return Expression.make(vm, Special.UNDEFINED);
        }));
    }

}
