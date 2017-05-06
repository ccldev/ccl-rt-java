package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;
import ccl.rt.store.Scope;
import cpa.subos.io.IOBase;

public interface Runner {
	
	Value execute(IVM vm, Scope s);
	Runner create();
	void creation(IOBase<?> cclCode);
	
}
