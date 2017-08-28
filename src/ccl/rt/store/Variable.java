package ccl.rt.store;

import java.util.List;

import ccl.rt.Value;
import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;
import io.github.coalangsoft.lib.dynamic.DynamicObject;

public class Variable implements Value{

	private Scope scope;
	private String name;
	private Value val;

	public Variable(Scope scope, String name) {
		this.scope = scope;
		this.name = name;
		this.val = scope.loadDirect(name);
	}
	
	public Object getValue(){
		return val.getValue();
	}
	
	public void setValue(Value v){
		if(v instanceof Variable){
			setValue(((Variable) v).val);
		}else{
			scope.variables.put(name, v);
		}
	}

	@Override
	public Value getProperty(boolean asPrototype, String name) {
		if(name.equals("_")){
			return val;
		}
		return val.getProperty(asPrototype, name);
	}

	@Override
	public List<String> getProperties() {
		return val.getProperties();
	}

	@Override
	public Value invoke(Value... args) throws Exception {
		return val.invoke(args);
	}

	@Override
	public DynamicBoolean bool() {
		return val.bool();
	}

	@Override
	public DynamicDouble num() {
		return new DynamicObject(getValue()).num();
	}

//	@Override
//	public void setPrototype(Value proto) {
//		val.setPrototype(proto);
//	}
//
//	@Override
//	public String computeType() {
//		return val.computeType();
//	}

}
