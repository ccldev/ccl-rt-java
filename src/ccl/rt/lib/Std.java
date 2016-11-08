package ccl.rt.lib;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.lib.func.BindFunc;
import ccl.rt.lib.func.UnbindFunc;

public class Std {
	
	public static Value lss(Value a, Value b){
		try{
			double a1 = ((Number) a.getValue()).doubleValue();
			double b1 = ((Number) b.getValue()).doubleValue();
			return new Expression(a1 < b1);
		}catch(ClassCastException e){
			try {
				return b.getProperty("lss").invoke(a);
			} catch (Exception e1) {
				return new Err(e1);
			}
		}
	}
	
	public static Value gtr(Value a, Value b){
		try{
			double a1 = ((Number) a.getValue()).doubleValue();
			double b1 = ((Number) b.getValue()).doubleValue();
			return new Expression(a1 > b1);
		}catch(ClassCastException e){
			try {
				return b.getProperty("gtr").invoke(a);
			} catch (Exception e1) {
				return new Err(e1);
			}
		}
	}
	
	public static Value add(Value a, Value b){
		try{
			double a1 = ((Number) a.getValue()).doubleValue();
			double b1 = ((Number) b.getValue()).doubleValue();
			return new Expression(a1 + b1);
		}catch(ClassCastException e){
			try {
				return b.getProperty("add").invoke(a);
			} catch (Exception e1) {
				return new Err(e1);
			}
		}
	}
	
	public static Value sub(Value a, Value b){
		try{
			double a1 = ((Number) a.getValue()).doubleValue();
			double b1 = ((Number) b.getValue()).doubleValue();
			return new Expression(a1 - b1);
		}catch(ClassCastException e){
			try {
				return b.getProperty("sub").invoke(a);
			} catch (Exception e1) {
				return new Err(e1);
			}
		}
	}
	
	public static Value mul(Value a, Value b){
		try{
			double a1 = ((Number) a.getValue()).doubleValue();
			double b1 = ((Number) b.getValue()).doubleValue();
			return new Expression(a1 * b1);
		}catch(ClassCastException e){
			try {
				return b.getProperty("mul").invoke(a);
			} catch (Exception e1) {
				return new Err(e1);
			}
		}
	}
	
	public static Value div(Value a, Value b){
		try{
			double a1 = ((Number) a.getValue()).doubleValue();
			double b1 = ((Number) b.getValue()).doubleValue();
			return new Expression(a1 / b1);
		}catch(ClassCastException e){
			try {
				return b.getProperty("div").invoke(a);
			} catch (Exception e1) {
				return new Err(e1);
			}
		}
	}

	public static Value bind(Value func, Value[] args) {
		return new BindFunc(func, args);
	}
	
	public static Value unbind(Value func, Value arg) {
		return new UnbindFunc(func, ((Number) arg.getValue()).intValue());
	}
	
	public static Value for_(Value func, Value[] args){
		Array ret = new Array(0);
		if(args.length == 1){
			Array a = (Array) args[0].getValue();
			for(int i = 0; i < a.length(); i++){
				try {
					ret.pushValue(func.invoke(a.getExpression(i)));
				} catch (Exception e) {
					ret.pushValue(new Err(e));
				}
			}
		}else if(args.length == 2){
			long a = ((Number) args[0].getValue()).longValue();
			long b = ((Number) args[1].getValue()).longValue();
			for(long i = a; i <= b; i++){
				try {
					ret.pushValue(func.invoke(new Expression(i)));
				} catch (Exception e) {
					ret.pushValue(new Err(e));
				}
			}
		}else{
			return new Err(new RuntimeException("Unexpected param count: " + args.length));
		}
		return new ArrayValue(ret);
	}

	public static Value while0(Value func, Value condition) {
		Array a = new Array(0);
		try {
			while((Boolean) condition.invoke().getValue()){
				a.pushValue(func.invoke());
			}
		} catch (Exception e) {
			return new Err(e);
		}
		return new ArrayValue(a);
	}
	
}
