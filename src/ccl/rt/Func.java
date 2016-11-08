package ccl.rt;

public abstract class Func extends Expression{

	public Func() {
		super(Special.FUNCTION);
	}
	
	public abstract Value invoke(Value... args);

}
