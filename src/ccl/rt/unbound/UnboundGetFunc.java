package ccl.rt.unbound;

import io.github.coalangsoft.lib.data.Func;
import ccl.rt.Value;

public class UnboundGetFunc implements Func<Value, Value> {

	private String prop;

	public UnboundGetFunc(String prop){
		this.prop = prop;
	}
	
	@Override
	public Value call(Value p) {
		return p.getProperty(prop);
	}

}
