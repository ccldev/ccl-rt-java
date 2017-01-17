package ccl.rt.lib.func;

import ccl.rt.Func;
import ccl.rt.Tool;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.vm.IVM;

public class UnbindFunc extends Func {

	private Value func;
	private int skip;
	private IVM vm;

	public UnbindFunc(IVM vm, Value func, int skip) {
		super(vm);
		this.vm = vm;
		this.func = func;
		this.skip = skip;
	}

	@Override
	public Value invoke(Value... args) {
		try {
			return func.invoke(Tool.link(skip, args, new Value[0]));
		} catch (Exception e) {
			return new Err(vm, e);
		}
	}

}
