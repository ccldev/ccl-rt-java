package ccl.rt.err;

import ccl.rt.Expression;
import ccl.rt.vm.IVM;
import ccl.rt.vm.StackTraceFormer;

public class Err extends Expression{

	public Err(IVM vm, Exception value) {
		super(vm, StackTraceFormer.formException(value,vm));
	}
	
}
