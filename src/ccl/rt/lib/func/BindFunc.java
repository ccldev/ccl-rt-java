package ccl.rt.lib.func;

import ccl.rt.Func;
import ccl.rt.Tool;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.vm.IVM;

public class BindFunc extends Func {

	private Value func;
	private Value[] args0;
	private IVM vm;

	public BindFunc(IVM vm, Value func, Value[] args) {
		super(vm);
		this.vm = vm;
		this.func = func;
		this.args0 = args;
	}

	@Override
	public Value invoke(Value... args) {
		try {
			return func.invoke(Tool.link(0, args0, args));
		} catch (Exception e) {
			return new Err(vm, e);
		}
	}

}
