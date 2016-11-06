package ccl.jrt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import ccl.rt.Value;
import ccl.rt.err.Err;

public class J {

	public static Value invoke(Object o, Method[] methods, Value[] args) {
		ArrayList<Exception> errs = new ArrayList<Exception>();
		for(int i = 0; i < methods.length; i++){
			Method m = methods[i];
			if(m.getParameterCount() != args.length){
				continue;
			}
			try {
				return new JMethod(o, m).call(args);
			} catch (Exception e) {
				errs.add(e);
			}
		}
		return new Err(errs);
	}

}
