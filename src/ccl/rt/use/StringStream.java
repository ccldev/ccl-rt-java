package ccl.rt.use;

import java.io.IOException;
import java.io.InputStream;

public class StringStream extends InputStream{

	private String s;
	private int dex;

	public StringStream(String s){
		this.s = s;
		this.dex = 0;
	}
	
	@Override
	public int read() throws IOException {
		if(dex >= s.length()){
			return -1;
		}
		char r = s.charAt(dex);
		dex++;
		return r;
	}
	
}
