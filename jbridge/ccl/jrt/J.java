package ccl.jrt;

import ccl.csy.CCL;
import ccl.rt.vm.StackTraceFormer;
import io.github.coalangsoft.reflect.Clss;
import io.github.coalangsoft.reflect.MultipleCallable;
import io.github.coalangsoft.reflect.SingleCallable;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.vm.IVM;
import ccl.jrt.JMethod;

public class J {

	public static Value invokeClass(IVM vm, Clss clss, Value[] args){
		if (clss.base.isInterface()) {
			return J.invoke(vm, null, new Clss(Proxy.getProxyClass(
					new ClassLoader() {
						@Override
						public Class<?> loadClass(String name) throws ClassNotFoundException {
							return CCL.classFinder.find(name);
						}
					}, new Class[]{clss.base})
			), new Value[]{Expression.make(vm,
					new JInvocationHandler(vm, args[0]))});
		}
		return invoke(vm, null, clss, args);
	}

	public static Value invoke(IVM vm, Object o, MultipleCallable<?> methods, Value[] args) {
		ArrayList<Object> errs = new ArrayList<Object>();
		Object[] vals = new Object[args.length];
		for(int i = 0; i < vals.length; i++){
			vals[i] = args[i].getValue();
		}

		errs.add("Trying to invoke " + methods + " with arguments " + Arrays.toString(vals));
		for(int i = 0; i < methods.length(); i++){
			SingleCallable m = methods.at(i);
			if(m.getParameterCount() != args.length){
				continue;
			}
			try {
				return new JMethod(vm, o, m).call(args);
			} catch (Exception e) {
				errs.add(StackTraceFormer.formException(e, vm));
			}
		}
		return new Err(vm, new Exception(errs + ""));
	}

	public static Value[] pack(IVM vm, Object[] arr){
		Value[] ret = new Value[arr.length];
		for(int i = 0; i < arr.length; i++){
			ret[i] = Expression.make(vm, arr[i]);
		}
		return ret;
	}

}
