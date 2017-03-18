package ccl.rt.unbound;

import io.github.coalangsoft.lib.data.Func;
import ccl.rt.Value;

public class UnboundInvokeFunc implements Func<Value, Value> {

	private Value[] args;

	public UnboundInvokeFunc(Value[] args) {
		this.args = args;
	}

	@Override
	public Value call(Value p) {
		try {
			return p.invoke(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
