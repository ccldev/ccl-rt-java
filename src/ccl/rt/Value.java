package ccl.rt;

import java.util.List;

public interface Value {
	
	Value getProperty(String name);
	Object getValue();
	List<String> getProperties();
	Value invoke(Value... args) throws Exception;

	boolean bool();
	
}
