package ccl.jrt;

import io.github.coalangsoft.reflect.Clss;
import io.github.coalangsoft.reflect.Methods;
import io.github.coalangsoft.reflect.SpecificMethods;

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
	private SpecificMethods methods;
	private JClass innerClass;
	private Clss clss;
	
	public JExpression(Object o, Clss c, String name) {
		super(Special.INVALID);
		
		this.object = o;
		this.clss = c;
		Field f = getField(name);
		this.methods = getMethods(name);

		Clss[] classes = c.getDeclaredClasses().getRaw();
		for(int i = 0; i < classes.length; i++){
			if(classes[i].getSimpleName().equals(name)){
				innerClass = new JClass(classes[i]);
				break;
			}
		}
		
		if (methods.count() == 0 && f == null && innerClass == null) {
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
			return clss.base.getField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

	private SpecificMethods getMethods(String name) {
		return clss.method(name).listSpecific(object, false);
	}

	public Value invoke(Value... args) throws Exception {
		if(innerClass != null){
			return innerClass.invoke(args);
		}
		if (methods.count() == 0) {
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
