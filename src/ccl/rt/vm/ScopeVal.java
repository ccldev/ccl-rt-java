package ccl.rt.vm;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.store.Scope;

public class ScopeVal extends Expression {

	private Scope scope;

	public ScopeVal(IVM vm, Scope value) {
		super(vm, value);
		scope = value;
	}
	
	public Value getProperty(String prop){
		Value v = super.getProperty(prop);
		if(v.getProperty("type").getValue().equals("error")){
			return scope.load(prop);
		}
		return v;
	}

}
