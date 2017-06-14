package ccl.rt.lib;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.vm.IVM;

public class Environment {
	
	public static Value regex(IVM vm, Value regex){
		return Expression.make(vm, new Regex(regex.getValue().toString()));
	}
	
	public static Err error(IVM vm, Value v){
		return new Err(vm, new Exception(v + ""));
	}
	
	public static Value boolean_(IVM vm, Value v){
		return new Expression(vm, v.bool());
	}
	public static Value integer(IVM vm, Value v){
		return Expression.make(vm,
			(long) Double.parseDouble(v.getValue() + "")
		);
	}
	public static Value char2int(IVM vm, Value v){
		Object o = v.getValue();
		if(o instanceof Character){
			return Expression.make(vm, (int) ((Character) o).charValue());
		}
		if(o instanceof Number){
			return Expression.make(vm, ((Number) o).intValue());
		}
		return Expression.make(vm,
			(int) (v.getValue() + "").charAt(0)
		);
	}
	public static Value float_(IVM vm, Value v){
		return Expression.make(vm,
			Double.parseDouble(v.getValue() + "")
		);
	}
	public static Value array(IVM vm, Value v){
		Object val = v.getValue();
		return new ArrayValue(vm, array0(vm, val));
	}
	private static Array array0(IVM vm, Object val){
		if(val instanceof Array) return (Array) val;
		if(val instanceof Number) return new Array(vm, ((Number) val).intValue());
		if(val.getClass().isArray()){
			return Array.clone(vm, val);
		}
		return new Array(vm,new Value[]{Expression.make(vm,val)});
	}
	public static Value char_(IVM vm, Value v){
		return Expression.make(vm, char0(vm, v.getValue()));
	}

	private static Object char0(IVM vm, Object value) {
		if(value instanceof Number){
			return "" + (char) ((Number) value).intValue();
		}else if(value instanceof String){
			return "" + value.toString().charAt(0);
		}else return new Err(vm, new Exception(value + ""));
	}
	
	public static Value byte_(IVM vm, Value v){
		return Expression.make(vm, ((Number) v.getValue()).byteValue());
	}
	
}
