package ccl.rt.lib;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.err.Err;

public class Environment {
	
	public static Err error(Value v){
		return new Err(v);
	}
	
	public static Value boolean_(Value v){
		return new Expression(
			Boolean.parseBoolean(v.getValue() + "")
		);
	}
	public static Value integer(Value v){
		return new Expression(
			(long) Double.parseDouble(v.getValue() + "")
		);
	}
	public static Value float_(Value v){
		return new Expression(
			Double.parseDouble(v.getValue() + "")
		);
	}
	
}
