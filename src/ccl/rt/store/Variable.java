package ccl.rt.store;

import java.util.List;

import ccl.rt.Value;

public class Variable implements Value{

	private Scope scope;
	private String name;

	public Variable(Scope scope, String name) {
		this.scope = scope;
		this.name = name;
	}
	
	public Object getValue(){
		return val().getValue();
	}
	
	private Value val(){
		return scope.loadDirect(name);
	}
	
	public void setValue(Value v){
		if(v instanceof Variable){
			setValue(((Variable) v).val());
		}else{
			scope.variables.put(name, v);
		}
	}

	@Override
	public Value getProperty(String name) {
		return val().getProperty(name);
	}

	@Override
	public List<String> getProperties() {
		return val().getProperties();
	}

	@Override
	public Value invoke(Value... args) throws Exception {
		return val().invoke(args);
	}

}
