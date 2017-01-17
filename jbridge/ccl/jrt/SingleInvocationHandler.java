package ccl.jrt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class SingleInvocationHandler implements InvocationHandler {

	private String name;
	private Value value;
	private IVM vm;

	public SingleInvocationHandler(IVM vm, String name, Value value) {
		this.name = name;
		this.value = value;
		this.vm = vm;
	}

	@Override
	public Object invoke(Object o, Method m, Object[] args)
			throws Throwable {
		if(m.getName().equals(name)){
			return value.invoke(J.pack(vm, args == null ? new Object[0] : args)).getValue();
		}else{
			throw new RuntimeException("Unexpected name: " + m.getName());
		}
	}

}
