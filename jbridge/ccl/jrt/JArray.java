package ccl.jrt;

import java.lang.reflect.Array;

public class JArray {
	
	public static Object cast(Class<?> arrClass, ccl.rt.Array cclArray){
		Class<?> itemClass = arrClass.getComponentType();
		Object ret = Array.newInstance(itemClass, cclArray.length());
		for(int i = 0; i < cclArray.length(); i++){
			Object item = cclArray.get(i);
			if(item instanceof ccl.rt.Array && !itemClass.isInstance(item)){
				item = cast(itemClass, (ccl.rt.Array) item);
			}
			Array.set(ret, i, item);
		}
		return ret;
	}
	
}
