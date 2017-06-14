package coa.rt.scripting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class EvalSetup {
	
	public static Value reflectEvalSupport(IVM vm) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class<?> sem = Class.forName("javax.script.ScriptEngineManager");
		Class<?> es = EvalSupport.class;
		Constructor<?> c = es.getConstructor(IVM.class, sem);
		return (Value) c.newInstance(vm, sem.newInstance());
	}
	
}
