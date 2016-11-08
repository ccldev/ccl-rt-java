package ccl.rt.vm;

import java.io.InputStream;
import java.util.ArrayList;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
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
	public void m(final Runner r, final Factory<InputStream> f) {
		Func func = new Func(){

			@Override
			public Value invoke(Value... args) {
				ArrayValue arr = new ArrayValue(new Array(args));
				
				s = s.chain();
				s.reserve("parameters");
				s.load("parameters").setValue(arr);
				ArrayList<Value> oldRam = ram;
				ram = new ArrayList<Value>();
				Value v;
				try {
					v = r.create().execute(f.make(), CclVm.this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				ram = oldRam;
				s = s.parent();
				return v;
			}
			
		};
		
		put(func);
	}

	@Override
	public void call(boolean beforeParams, int paramCount) throws Exception {
		Value[] args = new Value[paramCount];
		Value method = null;
		if(beforeParams){
			method = pop();
		}
		for(int i = paramCount - 1; i >= 0; i--){
			args[i] = pop();
		}
		if(!beforeParams){
			method = pop();
		}
		
		Value v = method.invoke(args);
		ram.add(v);
	}

	@Override
	public void dup() {
		Value a = pop();
		ram.add(a);
		ram.add(a);
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

	@Override
	public void a(int size) {
		put(new ArrayValue(size));
	}

	@Override
	public void reserve(String var) {
		put(s.reserve(var));
	}

}
