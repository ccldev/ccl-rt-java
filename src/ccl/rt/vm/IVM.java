package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;

public interface IVM {
	
	void cScope() throws Exception;
	void oScope() throws Exception;
	void s(String string) throws Exception;
	void f(String floatn) throws Exception;
	void i(String integer) throws Exception;
	void m(Runner r, InputStream methoddf) throws Exception;
	
	void call(boolean beforeParams, int paramCount) throws Exception;
	void dup() throws Exception;
	Value pop() throws Exception;
	void load(String var);
	void here(int index);
	void put(Value property);
	
}
