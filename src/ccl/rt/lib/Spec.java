package ccl.rt.lib;

import io.github.coalangsoft.ifw.use.InterfaceWorld;
import io.github.coalangsoft.reflect.Clss;
import ccl.jrt.JClass;

public class Spec {
	
	public static JClass java(String classname) throws ClassNotFoundException{
		String n = InterfaceWorld.findNotAbstract(classname);
		if(n == null){
			n = classname;
		}
		return new JClass(Clss.make(n));
	}
	
}
