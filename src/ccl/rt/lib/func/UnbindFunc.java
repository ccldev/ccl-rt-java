package ccl.rt.lib.func;

import ccl.rt.Func;
import ccl.rt.Tool;
import ccl.rt.Value;
import ccl.rt.err.Err;

public class UnbindFunc extends Func {

	private Value func;
	private int skip;

	public UnbindFunc(Value func, int skip) {
		this.func = func;
		this.skip = skip;
	}

	@Override
	public Value invoke(Value... args) {
		try {
			return func.invoke(Tool.link(skip, args, new Value[0]));
		} catch (Exception e) {
			return new Err(e);
		}
	}

}
