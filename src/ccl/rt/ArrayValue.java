package ccl.rt;

import ccl.rt.v6.property.IProperty;
import ccl.rt.vm.IVM;
import ccl.rt.err.Err;

public class ArrayValue extends Expression {

	private IVM vm;

	private void init(){
		setProperty("length", Func.by(vm, (args) -> Expression.make(vm, ((Array) ArrayValue.this.getValue()).length())));
		setProperty("push", Func.by(vm, (args) -> {
			((Array) ArrayValue.this.getValue()).pushValue(args[0]);
			if(args.length >= 2){
				setProperty(args[1].getValue() + "", args[0]);
			}
			return ArrayValue.this;
		}));
		setProperty("get", Func.by(vm, (args) -> ((Array) ArrayValue.this.getValue()).getExpression(((Number) args[0].getValue()).intValue())));
		setProperty("getOrDefault", Func.by(vm, (args) -> {
			try {
				return ((Array) getValue()).getExpression(((Number) args[0].getValue()).intValue());
			} catch (IndexOutOfBoundsException e) {
				return args[1];
			}
		}));
		setProperty("set", Func.by(vm, (args) -> ((Array) ArrayValue.this.getValue()).setValue(((Number) args[0].getValue()).intValue(), args[1])));
		setProperty("cut", Func.by(vm, (args) -> new ArrayValue(vm,
			((Array) getValue()).cut(((Number) args[0].getValue()).intValue())
		)));
		setProperty("remove", Func.by(vm, (args) -> ((Array) getValue()).remove(((Number) args[0].getValue()).intValue())));
		setProperty("add", Func.by(vm, (args) -> ((Array) ArrayValue.this.getValue()).operate("add")));
		setProperty("mul", Func.by(vm, (args) -> ((Array) ArrayValue.this.getValue()).operate("mul")));
		setProperty("select", Func.by(vm, (args) -> {
			try {
				return ((Array) ArrayValue.this.getValue()).select(args[0]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		setProperty("repeat", Func.by(vm, (args) -> {
			try {
				return ((Array) ArrayValue.this.getValue()).repeat();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		setProperty("sort", Func.by(vm, (args) -> {
			try {
				return new ArrayValue(vm, ((Array) ArrayValue.this.getValue()).sort());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		setProperty("zip", Func.by(vm, (args) -> {
			try {
				return ((Array) ArrayValue.this.getValue()).zip(args[0]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		setProperty("link", Func.by(vm, (args) -> {
			try {
				Array a = ((Array) ArrayValue.this.getValue()).link(
						(Array) args[0].getValue(),
						true
				);
				return new ArrayValue(vm,a);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		setProperty("toString", Func.by(vm, (args) -> Expression.make(vm, this + "")));
	}
	
	public ArrayValue(IVM vm, int size) {
		this(vm, new Array(vm, size));
		this.vm = vm;
		init();
	}
	
	public ArrayValue(IVM vm, Array a){
		super(vm, (args) -> a);
		init();
	}
	
	public Value getProperty0(boolean asPrototype, String prop){
		try{
			double d = Double.parseDouble(prop);
			return ((Array) ArrayValue.this.getValue()).getExpression((int) d);
		}catch(NumberFormatException e){
			return super.getProperty0(asPrototype, prop);
		}
	}

}
