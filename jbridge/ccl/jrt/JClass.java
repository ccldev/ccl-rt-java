package ccl.jrt;

import ccl.rt.Expression;
import ccl.rt.Value;

public class JClass extends Expression{
	
	private Class<?> clss;
	
	public JClass(Class<?> value) {
		super(value);
		clss = value;
	}
	
	public Value getProperty(String name){
		return new JExpression(null, clss, name);
	}
	
	public Value invoke(Value... args){
		return J.invoke(null, Call.pack(clss.getConstructors()), args);
	}

}
