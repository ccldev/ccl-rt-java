package ccl.rt.vm;

import java.io.InputStream;
import java.util.ArrayList;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.lib.Spec;
import ccl.rt.store.Scope;

public class CclVm implements IVM {

	private Scope s;
	private ArrayList<Value> ram;
	
	public CclVm(){
		s = new Scope();
		ram = new ArrayList<Value>();
		
		initStd();
		initSpec();
	}
	
	private void initSpec() {
		s.load("java").setValue(new Func(){
			@Override
			public Value invoke(Value... args) {
				try {
					return Spec.java(args[0].getValue() + "");
				} catch (ClassNotFoundException e) {
					return new Err(e);
				}
			}
		});
	}

	private void initStd() {
		s.load("false").setValue(new Expression(false));
		s.load("true").setValue(new Expression(true));
	}

	@Override
	public void cScope() {
		s = s.parent();
	}

	@Override
	public void oScope() {
		s = s.chain();
	}

	@Override
	public void s(String string) {
		ram.add(new Expression(string));
	}

	@Override
	public void f(String floatn) {
		ram.add(new Expression(Double.parseDouble(floatn)));
	}

	@Override
	public void i(String integer) {
		ram.add(new Expression(Long.parseLong(integer)));
	}

	@Override
	public void m(Runner r, InputStream methoddf) {
		throw new RuntimeException("NI");
	}

	@Override
	public void call(boolean beforeParams, int paramCount) throws Exception {
		Value[] args = new Value[paramCount];
		Value method = null;
		if(beforeParams){
			method = ram.remove(ram.size() - 1);
		}
		for(int i = paramCount - 1; i >= 0; i--){
			args[i] = ram.remove(ram.size() - 1);
		}
		if(!beforeParams){
			method = ram.remove(ram.size() - 1);
		}
		
		Value v = method.invoke(args);
		ram.add(v);
	}

	@Override
	public void dup() {
		ram.add(ram.get(ram.size() - 1));
	}

	@Override
	public Value pop() {
		return ram.remove(ram.size() - 1);
	}

	@Override
	public void load(String var) {
		ram.add(s.load(var));
	}

	@Override
	public void here(int index) {
		ram.add(ram.remove(ram.size() - (index + 1)));
	}

	@Override
	public void put(Value v) {
		ram.add(v);
	}

}
