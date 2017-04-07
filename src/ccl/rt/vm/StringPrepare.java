package ccl.rt.vm;

public class StringPrepare {

	public static char unescape(char c) {
		switch(c){
		case 't': return '\t';
		case 'n': return '\n';
		default: return c;
		}
	}
	
	public static String prepare(String string) {
		StringBuilder b = new StringBuilder();
		boolean unescape = false;
		for(int i = 0; i < string.length(); i++){
			char c = string.charAt(i);
			if(unescape){
				b.append(unescape(c));
				unescape = false;
			}else if(c == '\\'){
				unescape = true;
			}else{
				b.append(c);
			}
		}
		return b.toString();
	}
	
}
