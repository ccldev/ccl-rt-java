package ccl.rt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ccl.jrt.JExpression;
import ccl.rt.err.Err;

public class Expression implements Value {

	private Object value;
	
	private ArrayList<String> propList;
	private HashMap<String, Value> properties;
	
	protected void setProperty(String name, Value value){
		if(!propList.contains(name)){
			propList.add(name);
		}
		properties.put(name, value);
	}
	
	protected void setValue(Object value){
		this.value = value;
	}
	
	protected boolean contains(String name){
		return properties.get(name) != null;
	}
	
	public Expression(Object value) {
		this.value = value;
		this.propList = new ArrayList<String>();
		this.properties = new HashMap<String, Value>();
	}

	protected void initStdProperties() {
		System.out.println("TODO: Init std properties!");
	}

	public Value getProperty(String name) {
		Value v = properties.get(name);
		if(v == null){
			return new JExpression(value, value == null ? null : value.getClass(), name);
		}
		return v;
	}

	public Object getValue() {
		return value;
	}

	public final List<String> getProperties() {
		return propList;
	}

	public Value invoke(Value... args) throws Exception {
		if(args.length == 0){
			return this;
		}else if(args.length == 1){
			return getProperty(args[0].getValue() + "");
		}else{
			return new Err("Unsupported param count: " + args.length);
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + " [value=" + value + ", propList=" + propList
				+ ", properties=" + properties + "]";
	}

}
