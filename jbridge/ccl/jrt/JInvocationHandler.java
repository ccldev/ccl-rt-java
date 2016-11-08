package ccl.jrt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ccl.rt.Value;

public class JInvocationHandler implements InvocationHandler{

	private Value base;
	
	public JInvocationHandler(Value base){
		this.base = base;
	}
	
	@Override
	public Object invoke(Object o, Method m, Object[] args)
			throws Throwable {
		return base.getProperty(m.getName()).invoke(J.pack(args == null ? new Object[0] : args)).getValue();
	}

}
