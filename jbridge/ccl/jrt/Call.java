package ccl.jrt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Call {
	
	public static ICallable make(final Constructor<?> c){
		return new ICallable(){
			@Override
			public Object invoke(Object o, Object[] args) throws Exception {
				return c.newInstance(args);
			}

			@Override
			public Class<?>[] getParameterTypes() {
				return c.getParameterTypes();
			}

			@Override
			public int getParameterCount() {
				return getParameterTypes().length;
			}
		};
	}
	
	public static ICallable make(final Method m){
		return new ICallable(){
			@Override
			public Object invoke(Object o, Object[] args) throws Exception {
				Object r = m.invoke(o, args);
				if(r == null){
					return o;
				}
				return r;
			}
			
			@Override
			public Class<?>[] getParameterTypes() {
				return m.getParameterTypes();
			}

			@Override
			public int getParameterCount() {
				return getParameterTypes().length;
			}
		};
	}

	public static ICallable[] pack(Method[] methods) {
		ICallable[] ret = new ICallable[methods.length];
		for(int i = 0; i < methods.length; i++){
			ret[i] = make(methods[i]);
		}
		return ret;
	}
	
	public static ICallable[] pack(Constructor<?>[] constructors) {
		ICallable[] ret = new ICallable[constructors.length];
		for(int i = 0; i < constructors.length; i++){
			ret[i] = make(constructors[i]);
		}
		return ret;
	}
	
}
