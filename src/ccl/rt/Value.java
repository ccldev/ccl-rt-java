package ccl.rt;

import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;

import java.util.List;

public interface Value {
	
	Value getProperty(String name);
	Object getValue();
	List<String> getProperties();
	Value invoke(Value... args) throws Exception;

	DynamicBoolean bool();
	DynamicDouble num();
	
}
