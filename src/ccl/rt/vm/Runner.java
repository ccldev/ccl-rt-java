package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;

public interface Runner {
	
	Value execute(IVM vm);
	Runner create();
	void creation(InputStream cclCode);
	
}
