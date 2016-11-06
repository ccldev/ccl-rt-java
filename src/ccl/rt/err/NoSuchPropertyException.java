package ccl.rt.err;

import ccl.rt.Value;

public class NoSuchPropertyException extends RuntimeException {
	
	public NoSuchPropertyException(Value v, String name) {
		super("Unable to load property '" + name + "' from Value " + v);
	}

	private static final long serialVersionUID = 5033428580874343306L;

}
