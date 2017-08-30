package ccl.jrt;

import ccl.rt.*;
import io.github.coalangsoft.reflect.Clss;
import io.github.coalangsoft.reflect.SingleCallable;

import java.lang.reflect.Proxy;

import ccl.rt.vm.IVM;

public class JMethod {

	private SingleCallable method;
	private Object object;
	private IVM vm;

	public JMethod(IVM vm, Object o, SingleCallable m) {
		this.vm = vm;
		this.method = m;
		this.object = o;
	}

	public Value call(Value[] args) throws Exception {
		Clss[] ptypes = method.getParameterTypes().getRaw();
		Object[] arr = new Object[args.length];
		return Expression.make(vm, method.call(JCast.cast(vm,object,args, ptypes)));
	}

}
