package ccl.rt;

public class ArrayValue extends Expression {

	private void init(){
		setProperty("push", new Func(){
			@Override
			public Value invoke(Value... args) {
				((Array) ArrayValue.this.getValue()).pushValue(args[0]);
				if(args.length >= 2){
					ArrayValue.this.setProperty(args[1].getValue() + "", args[0]);
				}
				return ArrayValue.this;
			}
		});
		setProperty("get", new Func(){
			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).getExpression(((Number) args[0].getValue()).intValue());
			}
		});
		setProperty("set", new Func(){
			@Override
			public Value invoke(Value... args) {
				return ((Array) ArrayValue.this.getValue()).setValue(((Number) args[0].getValue()).intValue(), args[1]);
			}
		});
	}
	
	public ArrayValue(int size) {
		super(new Array(size));
		init();
	}
	
	public ArrayValue(Array a){
		super(a);
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
