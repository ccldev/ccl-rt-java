package ccl.rt;

public abstract class Func extends Expression{

	public Func() {
		super(Special.INVALID);
		setValue(new io.github.coalangsoft.lib.data.Func<Object, Object>(){

			@Override
			public Object call(Object p) {
				return invoke(new Expression(p)).getValue();
			}
			
		});
	}
	
	public abstract Value invoke(Value... args);

}
