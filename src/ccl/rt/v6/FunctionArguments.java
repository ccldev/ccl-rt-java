package ccl.rt.v6;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class FunctionArguments {

    private final Value[] args;

    public FunctionArguments(Value[] args){
        this.args = args;
    }

    public Value get(Func f, int index){
        if(args.length <= index){
            throw new RuntimeException("Not enough arguments: " + f + " needs " + (index - 1) + " but got " + args.length);
        }
        return args[index];
    }

    public Value getOrUndefined(IVM vm, int index){
        if(args.length <= index){
            return Expression.make(vm, Special.UNDEFINED);
        }
        return args[index];
    }

}
