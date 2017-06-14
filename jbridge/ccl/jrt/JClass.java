package ccl.jrt;

import ccl.csy.CCL;
import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.reflect.Clss;

import java.lang.reflect.Proxy;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class JClass extends Expression {

	private Clss clss;
	private IVM vm;

	public JClass(IVM vm, Clss value) {
		super(vm, new Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return value;
			}
		});
		this.vm = vm;
		clss = value;
	}

	public Value getProperty(String name) {
		if (getProperties().contains(name)) {
			return super.getProperty(name);
		}else{
			Clss inner = clss.getDeclaredClasses().bySimpleName(name);
			if(inner != null){
				return new JClass(vm, inner);
			}
		}
		return new JExpression(vm, null, clss, name);
	}

	public Value invoke(Value... args) {
		if (clss.base.isInterface()) {
			return J.invoke(vm, null, new Clss(Proxy.getProxyClass(
					new ClassLoader() {
						@Override
						public Class<?> loadClass(String name) throws ClassNotFoundException {
							return CCL.classFinder.find(name);
						}
					}, new Class[]{clss.base})
					), new Value[] { Expression.make(vm,
					new JInvocationHandler(vm, args[0])) });
		}
		
		return J.invoke(vm, null, clss, args);
	}

}
