package ccl.rt.err;

import ccl.rt.Expression;
import ccl.rt.vm.IVM;
import ccl.rt.vm.StackTraceFormer;
import io.github.coalangsoft.lib.data.Func;

public class Err extends Expression{

	public Err(IVM vm, Exception value) {
		super(vm, new Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return StackTraceFormer.formException(value,vm);
			}
		});
	}
	
}
