package ccl.rt.lib;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.lib.func.BindFunc;
import ccl.rt.lib.func.UnbindFunc;
import ccl.rt.store.Variable;
import ccl.rt.MapValue;
import ccl.rt.IProperty;
import ccl.rt.vm.IVM;

public class Std {
	
	public static Value lss(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().lss(b.num()));
	}
	
	public static Value gtr(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().gtr(b.num()));
	}
	
	public static Value add(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().add(b.num()));
	}
	
	public static Value sub(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().sub(b.num()));
	}
	
	public static Value mul(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().mul(b.num()));
	}
	
	public static Value div(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().div(b.num()));
	}
	
	public static Value pow(IVM vm, Value a, Value b){
		return new Expression(vm, a.num().pow(b.num()));
	}

	public static Value equals(IVM vm, Value a, Value b){
		return Expression.make(vm, a.getValue().equals(b.getValue()));
	}

	public static Value bind(IVM vm, Value func, Value[] args) {
		return new BindFunc(vm, func, args);
	}
	
	public static Value unbind(IVM vm, Value func, Value arg) {
		return new UnbindFunc(vm, func, ((Number) arg.getValue()).intValue());
	}
	
	public static Value forGlobal(IVM vm, Value func, Value[] args){
		if(args.length == 1){
			Array a = (Array) args[0].getValue();
			for(int i = 0; i < a.length(); i++){
				try {
					Value o = func.invoke(a.getExpression(i));
					if(o.getValue() != Special.UNDEFINED){
						return o;
					}
				} catch (Exception e) {}
			}
		}else if(args.length == 2){
			long a = ((Number) args[0].getValue()).longValue();
			long b = ((Number) args[1].getValue()).longValue();
			for(long i = a; i <= b; i++){
				try {
					Value o = func.invoke(Expression.make(vm, i));
					if(o.getValue() != Special.UNDEFINED){
						return o;
					}
				} catch (Exception e) {}
			}
		}else{
			return new Err(vm, new RuntimeException("Unexpected params count: " + args.length));
		}
		return Expression.make(vm, Special.UNDEFINED);
	}
	
	public static Value for_(IVM vm, Value func, Value[] args){
		Array ret = new Array(vm, 0);
		if(args.length == 1){
			Array a = (Array) args[0].getValue();
			for(int i = 0; i < a.length(); i++){
				try {
					ret.pushValue(func.invoke(a.getExpression(i)));
				} catch (Exception e) {
					ret.pushValue(new Err(vm, e));
				}
			}
		}else if(args.length == 2){
			long a = ((Number) args[0].getValue()).longValue();
			long b = ((Number) args[1].getValue()).longValue();
			for(long i = a; i <= b; i++){
				try {
					ret.pushValue(func.invoke(Expression.make(vm, i)));
				} catch (Exception e) {
					ret.pushValue(new Err(vm, e));
				}
			}
		}else{
			return new Err(vm, new RuntimeException("Unexpected params count: " + args.length));
		}
		return new ArrayValue(vm, ret);
	}

	public static Value while0(IVM vm, Value func, Value condition) {
		Array a = new Array(vm, 0);
		try {
			while(condition.invoke().bool().get()){
				a.pushValue(func.invoke());
			}
		} catch (Exception e) {
			return new Err(vm, e);
		}
		return new ArrayValue(vm, a);
	}
	
	public static Value whileGlobal(IVM vm, Value func, Value condition) throws Exception {
		while(condition.invoke().bool().get()){
			try {
				Value o = func.invoke();
				if(o.getValue() != Special.UNDEFINED){
					return o;
				}
			} catch (Exception e) {}
		}
		return Expression.make(vm, Special.UNDEFINED);
	}
	
	public static Value ifGlobal(IVM vm, Value func, Value els, Value condition) throws Exception {
		if(condition.invoke().bool().get()){
			try {
				Value o = func.invoke();
				if(o.getValue() != Special.UNDEFINED){
					return o;
				}
			} catch (Exception e) {}
		}else{
			try {
				Value o = els.invoke();
				if(o.getValue() != Special.UNDEFINED){
					return o;
				}
			} catch (Exception e) {}
		}
		return Expression.make(vm, Special.UNDEFINED);
	}

	public static Value not(IVM vm, Value expression) {
		return new Expression(vm, expression.bool().not());
	}

	public static Value mod(IVM vm, Value a, Value b) {
		return new Expression(vm, a.num().mod(b.num()));
	}

	public static Value concat(IVM vm, Value arg, Value arg1) {
		return Expression.make(vm, ((String) arg.getValue()) + arg1.getValue());
	}

	public static Value map(IVM vm){
		return new MapValue(vm);
	}

    public static Value set(IVM ivm, Value var, Value newVal) {
		if(var instanceof IProperty){
			((IProperty) var).setValue(newVal);
			return newVal;
		}else if(var instanceof Variable){
			((Variable) var).setValue(newVal);
			return newVal;
		}else{
			throw new RuntimeException("NIy");
		}
    }
}
