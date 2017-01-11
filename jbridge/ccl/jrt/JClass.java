package ccl.jrt;

import io.github.coalangsoft.reflect.Clss;

import java.lang.reflect.Proxy;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Value;

public class JClass extends Expression {

	private Clss clss;

	public JClass(Clss value) {
		super(value);
		clss = value;
	}

	public Value getProperty(String name) {
		if (getProperties().contains(name)) {
			return super.getProperty(name);
		}else{
			Clss inner = clss.getDeclaredClasses().bySimpleName(name);
			if(inner != null){
				return new JClass(inner);
			}
		}
		return new JExpression(null, clss, name);
	}

	public Value invoke(Value... args) {
		if (clss.base.isInterface()) {
			return J.invoke(null, Call.pack(Proxy.getProxyClass(
					ClassLoader.getSystemClassLoader(), new Class[] { clss.base })
					.getConstructors()), new Value[] { new Expression(
					new JInvocationHandler(args[0])) });
		}
		
		return J.invoke(null, Call.pack(clss.base.getConstructors()), args);
	}

}
