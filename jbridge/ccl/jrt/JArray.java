package ccl.jrt;

import java.lang.reflect.Array;

public class JArray {
	
	public static Object cast(Class<?> arrClass, ccl.rt.Array cclArray){
		Class<?> itemClass = arrClass.getComponentType();
		Object ret = Array.newInstance(itemClass, cclArray.length());
		for(int i = 0; i < cclArray.length(); i++){
			Array.set(ret, i, cclArray.get(i));
		}
		return ret;
	}
	
}
