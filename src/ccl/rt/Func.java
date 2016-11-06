package ccl.rt;

public abstract class Func extends Expression{

	public Func() {
		super(Special.UNDEFINED);
	}
	
	public abstract Value invoke(Value... args);

}
