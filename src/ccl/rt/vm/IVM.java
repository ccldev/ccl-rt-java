package ccl.rt.vm;

import cpa.subos.io.IOBase;
import io.github.coalangsoft.lib.data.Func;

import java.io.InputStream;

import ccl.rt.Value;
import ccl.rt.store.Scope;

public interface IVM {
	
	Scope cScope(Scope sc) throws Exception;
	Scope oScope(Scope sc) throws Exception;
	void s(String string) throws Exception;
	void f(String floatn) throws Exception;
	void i(String integer) throws Exception;
	void m(Runner r, Func<Void,IOBase<?>> s, Scope sc) throws Exception;
	void a();
	
	void call(int paramCount) throws Exception;
	void dup() throws Exception;
	Value pop() throws Exception;
	void load(String var, Scope s);
	void put(Value property);
	void reserve(String var, Scope s);
	
	void sPut(String funcName);
	String sPop();
	int sSize();
	boolean isDebugState();
	
}
