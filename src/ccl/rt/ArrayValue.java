package ccl.rt;

import ccl.rt.vm.IVM;
import ccl.rt.err.Err;

public class ArrayValue extends Expression {

	private IVM vm;

	private void init(){
		setProperty("push", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				((Array) ArrayValue.this.getValue()).pushValue(args[0]);
				if(args.length >= 2){
					ArrayValue.this.setProperty(args[1].getValue() + "", args[0]);
				}
				return ArrayValue.this;
			}
		});
		setProperty("get", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).getExpression(((Number) args[0].getValue()).intValue());
			}
		});
		setProperty("getOrDefault", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				try{
					return ((Array) ArrayValue.this.getValue()).getExpression(((Number) args[0].getValue()).intValue());
				}catch(IndexOutOfBoundsException e){
					return args[1];
				}
			}
		});
		setProperty("set", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).setValue(((Number) args[0].getValue()).intValue(), args[1]);
			}
		});
		setProperty("cut", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				return new ArrayValue(vm, 
					((Array) ArrayValue.this.getValue()).cut(((Number) args[0].getValue()).intValue())
				);
			}
			
		});
		setProperty("remove", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).remove(((Number) args[0].getValue()).intValue());
			}
			
		});
		setProperty("add", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).operate("add");
			}
			
		});
		setProperty("mul", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).operate("mul");
			}
			
		});
		setProperty("select", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				try {
					return ((Array) ArrayValue.this.getValue()).select(args[0]);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		});
		setProperty("repeat", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				try {
					return ((Array) ArrayValue.this.getValue()).repeat();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		});
		setProperty("sort", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				try {
					return new ArrayValue(vm, ((Array) ArrayValue.this.getValue()).sort());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		});
		setProperty("zip", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				try {
					return ((Array) ArrayValue.this.getValue()).zip(args[0]);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		});
		setProperty("link", new Func(vm){

			@Override
			public Value invoke(Value... args) {
				try {
					Array a = ((Array) ArrayValue.this.getValue()).link(
							(Array) args[0].getValue(),
							true
					);
					return new ArrayValue(vm,a);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		});
	}
	
	public ArrayValue(IVM vm, int size) {
		this(vm, new Array(vm, size));
		this.vm = vm;
		init();
	}
	
	public ArrayValue(IVM vm, Array a){
		super(vm, new io.github.coalangsoft.lib.data.Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return a;
			}
		});
		init();
	}
	
	public Value getProperty(String prop){
		try{
			double d = Double.parseDouble(prop);
			return ((Array) ArrayValue.this.getValue()).getExpression((int) d);
		}catch(NumberFormatException e){
			return super.getProperty(prop);
		}
	}

}
