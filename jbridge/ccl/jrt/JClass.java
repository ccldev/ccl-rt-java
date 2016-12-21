package ccl.jrt;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Value;

public class JClass extends Expression {

	private Class<?> clss;

	public JClass(Class<?> value) {
		super(value);
		clss = value;
	}

	public Value getProperty(String name) {
		if (getProperties().contains(name)) {
			return super.getProperty(name);
		}
		return new JExpression(null, clss, name);
	}

	public Value invoke(Value... args) {
		if (clss.isInterface()) {
			return J.invoke(null, Call.pack(Proxy.getProxyClass(
					clss.getClassLoader(), new Class[] { clss })
					.getConstructors()), new Value[] { new Expression(
					new JInvocationHandler(args[0])) });
		}
		
		return J.invoke(null, Call.pack(clss.getConstructors()), args);
	}

}
