package ccl.rt.vm;

import java.io.InputStream;

import ccl.rt.Value;

public interface IVM {
	
	void cScope() throws Exception;
	void oScope() throws Exception;
	void s(String string) throws Exception;
	void f(String floatn) throws Exception;
	void i(String integer) throws Exception;
	void m(Runner r, Factory<InputStream> s) throws Exception;
	void a(int size);
	
	void call(int paramCount) throws Exception;
	void dup() throws Exception;
	Value pop() throws Exception;
	void load(String var);
	void put(Value property);
	void reserve(String var);
	
}
