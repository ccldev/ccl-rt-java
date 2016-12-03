package ccl.rt.vm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.lib.Spec;
import ccl.rt.store.Scope;

public class CclVm implements IVM {

	private HashMap<Thread,Scope> scopes;
	
	private Scope glob;
	
	private HashMap<Thread,ArrayList<Value>> rams;
	
	private ArrayList<Value> ram(){
		Thread t = Thread.currentThread();
		ArrayList<Value> ret = rams.get(t);
		if(ret != null){
			return ret;
		}else{
			ArrayList<Value> n = new ArrayList<Value>();
			rams.put(t,n);
			return n;
		}
	}
	private void setRam(ArrayList<Value> ram){
		rams.put(Thread.currentThread(), ram);
	}
	private Scope scope(){
		Thread t = Thread.currentThread();
		Scope ret = scopes.get(t);
		if(ret != null){
			return ret;
		}else{
			Scope n = glob.chain();
			scopes.put(t,n);
			return n;
		}
	}
	private void setScope(Scope s){
		scopes.put(Thread.currentThread(), s);
	}
	
	public CclVm(){
		glob = new Scope();
		
		initStd(glob);
		initSpec(glob);
		
		rams = new HashMap<Thread,ArrayList<Value>>();
		scopes = new HashMap<Thread,Scope>();
		scopes.put(Thread.currentThread(), glob);
	}
	
	public void setVariable(String name, Object value){
		scope().load(name).setValue(new Expression(value));
	}
	
	private void initSpec(Scope s) {
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
		s.load("scope").setValue(new ScopeVal(s));
	}

	private void initStd(Scope s) {
		s.load("false").setValue(new Expression(false));
		s.load("true").setValue(new Expression(true));
	}

	@Override
	public void cScope() {
		setScope(scope().parent());
	}

	@Override
	public void oScope() {
		setScope(scope().chain());
	}

	@Override
	public void s(String string) {
		ram().add(new Expression(prepare(string)));
	}

	private String prepare(String string) {
		StringBuilder b = new StringBuilder();
		boolean unescape = false;
		for(int i = 0; i < string.length(); i++){
			char c = string.charAt(i);
			if(unescape){
				b.append(unescape(c));
				unescape = false;
			}else if(c == '\\'){
				unescape = true;
			}else{
				b.append(c);
			}
		}
		return b.toString();
	}

	private char unescape(char c) {
		switch(c){
		case 't': return '\t';
		case 'n': return '\n';
		default: return c;
		}
	}

	@Override
	public void f(String floatn) {
		ram().add(new Expression(Double.parseDouble(floatn)));
	}

	@Override
	public void i(String integer) {
		ram().add(new Expression(Long.parseLong(integer)));
	}

	@Override
	public void m(final Runner r, final Factory<InputStream> f) {
		Func func = new Func(){

			@Override
			public Value invoke(Value... args) {
				ArrayValue arr = new ArrayValue(new Array(args));
				
				setScope(scope().chain());
				scope().reserve("parameters");
				scope().load("parameters").setValue(arr);
				ArrayList<Value> oldRam = ram();
				setRam(new ArrayList<Value>());
				Value v;
				try {
					v = r.create().execute(f.make(), CclVm.this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				setRam(oldRam);
				setScope(scope().parent());
				return v;
			}
			
		};
		
		put(func);
	}

	@Override
	public void call(int paramCount) throws Exception {
		Value[] args = new Value[paramCount];
		Value method = null;
		for(int i = paramCount - 1; i >= 0; i--){
			args[i] = pop();
		}
		method = pop();
		
		try{
			Value v = method.invoke(args);
			ram().add(v);
		}catch(RuntimeException e){
			ram().add(new Err(e));
		}
	}

	@Override
	public void dup() {
		Value a = pop();
		ram().add(a);
		ram().add(a);
	}

	@Override
	public Value pop() {
		return ram().remove(ram().size() - 1);
	}

	@Override
	public void load(String var) {
		ram().add(scope().load(var));
	}

	@Override
	public void put(Value v) {
		ram().add(v);
	}

	@Override
	public void a(int size) {
		put(new ArrayValue(size));
	}

	@Override
	public void reserve(String var) {
		put(scope().reserve(var));
	}

}
