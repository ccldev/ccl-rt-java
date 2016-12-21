package coa.std;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.err.Err;

public class NVP extends Func{
	
	private String name;
	private Value value;

	public NVP(String name, Value value){
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	public Value getValue(){
		return value;
	}
	
	public String formatName(String prefix){
		char uc = Character.toUpperCase(name.charAt(0));
		return prefix + uc + name.substring(1, name.length());
	}

	@Override
	public Value invoke(Value... args) {
		String n = formatName("set");
		for(int i = 0; i < args.length; i++){
			try {
				args[i].getProperty(n).invoke(value);
			} catch (Exception e) {
				return new Err(e);
			}
		}
		return new Expression(Special.UNDEFINED);
	}
	
}
