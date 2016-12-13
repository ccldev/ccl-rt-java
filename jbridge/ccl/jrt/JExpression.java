package ccl.jrt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.err.Err;

public class JExpression extends Expression {

	private Object object;
	private Method[] methods;
	private JClass innerClass;
	private Class<?> clss;
	
	public JExpression(Object o, Class<?> c, String name) {
		super(Special.INVALID);
		
		this.object = o;
		this.clss = c;
		Method[] m = getMethods(name);
		Field f = getField(name);
		this.methods = m;

		Class<?>[] classes = c.getDeclaredClasses();
		for(int i = 0; i < classes.length; i++){
			if(classes[i].getSimpleName().equals(name)){
				innerClass = new JClass(classes[i]);
				break;
			}
		}
		
		if (m.length == 0 && f == null && innerClass == null) {
			setValue(new Err(new RuntimeException("No such native property '"
					+ name + "' on Object " + o)));
			return;
		}

		if (f == null) {
			setValue(Special.UNDEFINED);
		} else {
			try {
				setValue(f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(
						"Implementation Exception: Field found but not found :(");
			}
		}
	}

	private Field getField(String name) {
		try {
			return clss.getField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

	private Method[] getMethods(String name) {
		Method[] ms = clss.getMethods();
		ArrayList<Method> list = new ArrayList<Method>();
		for (int i = 0; i < ms.length; i++) {
			if (ms[i].getName().equals(name)) {
				list.add(ms[i]);
			}
		}
		return list.toArray(new Method[0]);
	}

	public Value invoke(Value... args) throws Exception {
		if(innerClass != null){
			return innerClass.invoke(args);
		}
		if (methods.length == 0) {
			return super.invoke(args);
		} else {
			return J.invoke(object, Call.pack(methods), args);
		}
	}
	
	public Value getProperty(String s){
		if(innerClass != null){
			return innerClass.getProperty(s);
		}else{
			return super.getProperty(s);
		}
	}

}
