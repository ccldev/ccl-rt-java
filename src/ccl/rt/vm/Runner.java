package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;
import ccl.rt.store.Scope;

public interface Runner {
	
	Value execute(IVM vm, Scope s);
	Runner create();
	void creation(InputStream cclCode);
	
}
