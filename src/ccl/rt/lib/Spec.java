package ccl.rt.lib;

import io.github.coalangsoft.ifw.use.InterfaceWorld;
import io.github.coalangsoft.reflect.Clss;
import ccl.jrt.JClass;
import ccl.rt.vm.IVM;

public class Spec {
	
	public static JClass java(IVM vm, String classname) throws ClassNotFoundException{
		String n = InterfaceWorld.findNotAbstract(classname);
		if(n == null){
			n = classname;
		}
		return new JClass(vm, Clss.make(n));
	}
	
}
