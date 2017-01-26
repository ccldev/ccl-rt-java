package ccl.jrt;

import io.github.coalangsoft.reflect.Clss;
import io.github.coalangsoft.reflect.Methods;
import io.github.coalangsoft.reflect.SpecificMethods;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.vm.IVM;
import ccl.rt.vm.StackTraceFormer;

public class JExpression extends Expression {

	private Object object;
	private SpecificMethods methods;
	private JClass innerClass;
	private Clss clss;
	private IVM vm;
	
	public JExpression(IVM vm, Object o, Clss c, String name) {
		super(vm, Special.INVALID);
		this.vm = vm;
		
		this.object = o;
		this.clss = c;
		Field f = getField(name);
		this.methods = getMethods(name);

		Clss[] classes = c.getDeclaredClasses().getRaw();
		for(int i = 0; i < classes.length; i++){
			if(classes[i].getSimpleName().equals(name)){
				innerClass = new JClass(vm, classes[i]);
				break;
			}
		}
		
		if (methods.length() == 0 && f == null && innerClass == null) {
			setValue(new Err(vm, new RuntimeException("No such native property '"
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
		try{
			Value r;
			if(innerClass != null){
				r = innerClass.invoke(args);
			}else{
				r = J.invoke(vm, object, Call.pack(methods), args);
			}
			if(r instanceof Err){
				Object o = ((Err) r).getValue();
				if(o instanceof Throwable){
					((Throwable) o).printStackTrace(System.out);
				}else if(o instanceof List){
					List<Throwable> t = (List<Throwable>) o;
					for(int i = 0; i < t.size(); i++){
						t.get(i).printStackTrace(System.out);
					}
				}
			}
			return r;
		}catch(Exception e){
			throw new Exception("CCL Runtime Exception: Invoke Expression\n" + this, e);
		}
	}
	
	public Value getProperty(String s){
		if(innerClass != null){
			return innerClass.getProperty(s);
		}else{
			return super.getProperty(s);
		}
	}

	@Override
	public String toString() {
		return "JExpression [object=" + object + ", methods=" + methods
				+ ", innerClass=" + innerClass + ", clss=" + clss + ", vm="
				+ vm + "]";
	}

}
