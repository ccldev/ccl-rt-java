package ccl.jrt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ccl.rt.Value;
import ccl.rt.vm.IVM;
import ccl.rt.vm.StackTraceFormer;

public class JInvocationHandler implements InvocationHandler{
	
	private Value base;
	private IVM vm;
	
	public JInvocationHandler(IVM vm, Value base){
		this.vm = vm;
		this.base = base;
	}
	
	@Override
	public Object invoke(Object o, Method m, Object[] args)
			throws Throwable {
		try{
			return base.getProperty(m.getName()).invoke(J.pack(vm, args == null ? new Object[0] : args)).getValue();
		}catch(RuntimeException e){
			throw StackTraceFormer.formException(e, vm);
		}
	}

}
