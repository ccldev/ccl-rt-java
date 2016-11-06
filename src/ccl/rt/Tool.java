package ccl.rt;

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
	
}
