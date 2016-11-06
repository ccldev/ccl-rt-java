package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;
import ccl.rt.store.Scope;

public interface Runner {
	
	Value execute(Scope privateScope, InputStream cclCode, IVM vm);
	Runner create();
	
}
