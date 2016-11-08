package ccl.jrt;

public interface ICallable {
	
	Object invoke(Object o, Object[] args) throws Exception;
	Class<?>[] getParameterTypes();
	int getParameterCount();
	
}
