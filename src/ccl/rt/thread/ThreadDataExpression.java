package ccl.rt.thread;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class ThreadDataExpression extends Expression {

	private ThreadData data;
	private IVM vm;

	public ThreadDataExpression(IVM vm, Thread t) {
		super(vm, new io.github.coalangsoft.lib.data.Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return Special.UNDEFINED;
			}
		});
		this.vm = vm;
		this.data = new ThreadData(t);
		setValue(data);
	}
	
	public Value getProperty0(boolean asPrototype, String p){
		synchronized (this) {
			if(p.equals("write")){
				return new Func(vm){
					@Override
					public Value invoke(Value... args) {
						data.getShared().write(
							args[0].getValue().toString(),
							args[1].getValue().toString(),
							args[2]
						);
						return Expression.make(vm, Special.UNDEFINED);
					}
				};
			}else if(p.equals("read")){
				return new Func(vm){
					@Override
					public Value invoke(Value... args) {
						return data.getShared().read(
							args[0].getValue().toString()
						);
					}
				};
			}else{
				return super.getProperty0(asPrototype, p);
			}
		}
	}

}
