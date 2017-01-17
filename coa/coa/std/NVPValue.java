package coa.std;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class NVPValue extends Expression {

	public NVPValue(IVM vm, String name, Value val) {
		super(vm, new NVP(vm, name, val));
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
