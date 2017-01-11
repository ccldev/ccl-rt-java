package coa.std;

import ccl.rt.Expression;
import ccl.rt.Value;

public class NVPValue extends Expression {

	public NVPValue(String name, Value val) {
		super(new NVP(name, val));
	}
	
	public Value getProperty(String name){
		if(name.equals("getValue")){
			return ((NVP) getValue()).getB();
		}else{
			return super.getProperty(name);
		}
	}
	
	public Value invoke(Value... args){
		return ((NVP) getValue()).invoke(args);
	}

}
