package ccl.rt.lib;

import ccl.jrt.JClass;

public class Spec {
	
	public static JClass java(String classname) throws ClassNotFoundException{
		return new JClass(Class.forName(classname));
	}
	
}
