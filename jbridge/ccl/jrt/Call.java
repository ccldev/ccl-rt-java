package ccl.jrt;

import io.github.coalangsoft.reflect.ClassSequence;
import io.github.coalangsoft.reflect.SpecificMethod;
import io.github.coalangsoft.reflect.SpecificMethods;

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
			public ClassSequence getParameterTypes() {
				return ClassSequence.make(c.getParameterTypes());
			}

			@Override
			public int getParameterCount() {
				return getParameterTypes().length();
			}
		};
	}
	
	public static ICallable make(final SpecificMethod m){
		return new ICallable(){
			@Override
			public Object invoke(Object o, Object[] args) throws Exception {
				m.setAccessible(true);
				Object r = m.call(args);
				if(r == null){
					return o;
				}
				return r;
			}
			
			@Override
			public ClassSequence getParameterTypes() {
				return m.getParameterTypes();
			}

			@Override
			public int getParameterCount() {
				return getParameterTypes().length();
			}
		};
	}

	public static ICallable[] pack(SpecificMethods methods) {
		ICallable[] ret = new ICallable[methods.count()];
		for(int i = 0; i < methods.count(); i++){
			ret[i] = make(methods.get(i));
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
