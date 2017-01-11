package ccl.jrt;

import io.github.coalangsoft.reflect.ClassSequence;

public interface ICallable {
	
	Object invoke(Object o, Object[] args) throws Exception;
	ClassSequence getParameterTypes();
	int getParameterCount();
	
}
