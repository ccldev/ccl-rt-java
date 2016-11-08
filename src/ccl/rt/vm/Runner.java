package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;

public interface Runner {
	
	Value execute(InputStream cclCode, IVM vm);
	Runner create();
	
}
