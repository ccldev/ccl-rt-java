package ccl.rt.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	private String regex;

	public Regex(String regex) {
		this.regex = regex;
	}
	
	public Match match(String s){
		Matcher m = Pattern.compile(regex).matcher(s);
		if(m.matches()){
			return new Match(m);
		}else{
			return new Match();
		}
	}

}
