package ccl.rt.lib.func;

import ccl.rt.Func;
import ccl.rt.Tool;
import ccl.rt.Value;
import ccl.rt.err.Err;

public class BindFunc extends Func {

	private Value func;
	private Value[] args0;

	public BindFunc(Value func, Value[] args) {
		this.func = func;
		this.args0 = args;
	}

	@Override
	public Value invoke(Value... args) {
		try {
			return func.invoke(Tool.link(args0, args));
		} catch (Exception e) {
			return new Err(e);
		}
	}

}
