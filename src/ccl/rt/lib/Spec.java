package ccl.rt.lib;

import ccl.rt.v6.jrt.JClassExpression;
import ccl.rt.vm.StackTraceFormer;
import io.github.coalangsoft.ifw.use.InterfaceWorld;
import io.github.coalangsoft.lib.reflect.CustomClassFinder;
import io.github.coalangsoft.reflect.Clss;
import ccl.rt.vm.IVM;

public class Spec {
	
	public static JClassExpression java(IVM vm, CustomClassFinder f, String classname) throws ClassNotFoundException{
		String n = InterfaceWorld.findNotAbstract(classname, f);
		if(n == null){
			n = classname;
		}
		try {
			return new JClassExpression(vm, Clss.make(n, f));
		}catch(ClassNotFoundException e){
			StackTraceFormer.formException(e, vm).printStackTrace();
			throw e;
		}
	}
	
}
