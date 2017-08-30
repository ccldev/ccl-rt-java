package ccl.rt;

import ccl.rt.vm.IVM;

public class CoaCast {

    public static Value cast(IVM vm, Object value){
        if(value != null){
            if(value instanceof Array){
                return new ArrayValue(vm, (Array) value);
            }if(value.getClass().isArray()){
                return new ArrayValue(vm, Array.clone(vm, value));
            }

            if(value instanceof Boolean){
                value = ((Boolean) value).booleanValue() ? 1 : 0;
            }
            if(value instanceof Number){
                value = ((Number) value).doubleValue();
            }
            return Expression.make0(vm, value);
        }
        return Expression.make0(vm, Special.UNDEFINED);
    }

}
