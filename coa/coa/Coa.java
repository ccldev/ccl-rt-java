package coa;

import java.io.InputStream;
import java.net.URL;

public class Coa {
	
	public static InputStream streamResource(String name){
		return Coa.class.getResourceAsStream("/coalang/runtime/res/" + name);
	}
	public static URL urlResource(String name){
		return Coa.class.getResource("/coalang/runtime/res/" + name);
	}
	
}
