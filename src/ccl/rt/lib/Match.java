package ccl.rt.lib;

import java.util.regex.Matcher;

public class Match {

	public final boolean is;
	private Matcher matcher;
	
	public Match(Matcher m) {
		is = true;
		matcher = m;
	}

	public Match() {
		is = false;
	}
	
	public String group(int index){
		return matcher.group(index + 1);
	}
	
	public String toString(){
		return matcher.group(0);
	}

	public boolean is(){
		return is;
	}

}
