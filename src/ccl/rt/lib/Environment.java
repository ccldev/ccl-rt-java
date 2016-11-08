package ccl.rt.lib;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
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
	public static Value array(Value v){
		Object val = v.getValue();
		return new ArrayValue(array0(val));
	}
	public static Array array0(Object val){
		if(val instanceof Array) return (Array) val;
		if(val instanceof Number) return new Array(((Number) val).intValue());
		return Array.clone(val);
	}
	
}
