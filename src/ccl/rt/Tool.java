package ccl.rt;

import java.util.ArrayList;

public class Tool {
	
	public static String link(int skip, String[] array, String s){
		StringBuilder b = new StringBuilder();
		for(int i = skip; i < array.length; i++){
			b.append(array[i]);
			if(i < array.length - 1){
				b.append(s);
			}
		}
		return b.toString();
	}

	public static Value[] link(Value[] a, Value[] b) {
		ArrayList<Value> ret = new ArrayList<Value>();
		int i;
		for(i = 0; i < a.length; i++){
			ret.add(a[i]);
		}
		for(int j = 0; j < b.length; j++){
			ret.add(b[j]);
		}
		return ret.toArray(new Value[0]);
	}
	
}
