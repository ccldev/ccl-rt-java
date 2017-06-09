package ccl.rt.lib;

import ccl.rt.vm.StackTraceFormer;
import io.github.coalangsoft.ifw.use.InterfaceWorld;
import io.github.coalangsoft.lib.reflect.CustomClassFinder;
import io.github.coalangsoft.reflect.Clss;
import ccl.jrt.JClass;
import ccl.rt.vm.IVM;

public class Spec {
	
	public static JClass java(IVM vm, CustomClassFinder f, String classname) throws ClassNotFoundException{
		String n = InterfaceWorld.findNotAbstract(classname, f);
		if(n == null){
			n = classname;
		}
		try {
			return new JClass(vm, Clss.make(n, f));
		}catch(ClassNotFoundException e){
			StackTraceFormer.formException(e, vm).printStackTrace();
			throw e;
		}
	}
	
}
