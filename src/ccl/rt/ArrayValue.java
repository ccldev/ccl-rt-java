package ccl.rt;

import ccl.rt.vm.IVM;

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
	}
	
	public ArrayValue(IVM vm, int size) {
		super(vm, new Array(vm, size));
		this.vm = vm;
		init();
	}
	
	public ArrayValue(IVM vm, Array a){
		super(vm, a);
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
