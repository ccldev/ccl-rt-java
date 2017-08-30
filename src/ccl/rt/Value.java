package ccl.rt;

import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;

import java.util.List;

public interface Value {
	
	IProperty getProperty(boolean asPrototype, String name);
	Object getValue();
	List<String> getProperties();
	Value invoke(Value... args) throws Exception;

	DynamicBoolean bool();
	DynamicDouble num();

	//V6
	void setPrototype(Value proto);
	String computeType();
	
}
