package ccl.rt;

import ccl.rt.lib.SingleOperator;
import ccl.rt.lib.StandardOperator;
import ccl.rt.vm.IVM;

public abstract class Func extends Expression{

	public Func(final IVM vm) {
		super(vm, (a) -> Special.INVALID);
		setValue((io.github.coalangsoft.lib.data.Func) (a) -> invoke(Expression.make(vm, a)).getValue());
	}
	
	public abstract Value invoke(Value... args);

	public static Func by(IVM vm, io.github.coalangsoft.lib.data.Func<Value[], Value> action){
		return new Func(vm) {
			@Override
			public Value invoke(Value... args) {
				return action.call(args);
			}
		};
	}

	public static Func by(IVM vm, StandardOperator op){
		return new Func(vm) {
			@Override
			public Value invoke(Value... args) {
				return op.op(vm, args[0], args[1]);
			}
		};
	}

	public static Func by(IVM vm, SingleOperator op){
		return new Func(vm) {
			@Override
			public Value invoke(Value... args) {
				return op.op(vm, args[0]);
			}
		};
	}

}
