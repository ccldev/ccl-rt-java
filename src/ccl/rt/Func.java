package ccl.rt;

import ccl.rt.vm.IVM;

public abstract class Func extends Expression{

	public Func(final IVM vm) {
		super(vm, new io.github.coalangsoft.lib.data.Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return Special.INVALID;
			}
		});
		setValue(new io.github.coalangsoft.lib.data.Func<Object, Object>(){

			@Override
			public Object call(Object p) {
				return invoke(Expression.make(vm, p)).getValue();
			}
			
		});
	}
	
	public abstract Value invoke(Value... args);

}
