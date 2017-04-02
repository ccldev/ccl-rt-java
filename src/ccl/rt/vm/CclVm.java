package ccl.rt.vm;

import io.github.coalangsoft.lib.reflect.CustomClassFinder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import coa.std.NVP;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Unbound;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.store.Scope;

public class CclVm implements IVM {
	
	private boolean debugState = false;
	
	private HashMap<Thread,ArrayList<Value>> rams;
	private HashMap<Thread,Stack<String>> stacks;
	
	private Stack<String> stack(){
		Thread t = Thread.currentThread();
		Stack<String> ret = stacks.get(t);
		if(ret != null){
			return ret;
		}else{
			Stack<String> n = new Stack<String>();
			stacks.put(t,n);
			return n;
		}
	}
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
	
	public CclVm(){
		rams = new HashMap<Thread,ArrayList<Value>>();
		stacks = new HashMap<Thread,Stack<String>>();
	}

	@Override
	public Scope cScope(Scope sc) {
		return sc.parent();
	}

	@Override
	public Scope oScope(Scope sc) {
		return sc.chain();
	}

	@Override
	public void s(String string) {
		ram().add(new Expression(this, prepare(string)));
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
		ram().add(new Expression(this, Double.parseDouble(floatn)));
	}

	@Override
	public void i(String integer) {
		ram().add(new Expression(this, Long.parseLong(integer)));
	}

	@Override
	public void m(final Runner r, final io.github.coalangsoft.lib.data.Func<Void, InputStream> f, final Scope sc) {
		final Runner runner = r.create();
		runner.creation(f.call(null));
		
		Func func = new Func(this){
			
			@Override
			public Value invoke(Value... args) {
				ArrayValue arr = new ArrayValue(CclVm.this, new Array(CclVm.this, args));
				
				Scope myScope = sc.chain();
				myScope.reserve("parameters");
				myScope.load("parameters").setValue(arr);
				myScope.reserve("@");
				myScope.load("@").setValue(myScope.load("parameters"));
				ArrayList<Value> oldRam = ram();
				setRam(new ArrayList<Value>());
				Value v;
				try {
					v = runner.execute(CclVm.this, myScope);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				setRam(oldRam);
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
		
		ArrayList<Value> params = new ArrayList<Value>();
		ArrayList<Value> settings = new ArrayList<Value>();
		
		for(int i = 0; i < args.length; i++){
			Value v = args[i];
			if(v.getValue() instanceof NVP){
				settings.add(v);
			}else if(v instanceof Unbound){
				params.add(((Unbound) v).formFunction(this));
			}else{
				params.add(v);
			}
		}
		
		args = params.toArray(new Value[0]);
		
		try{
			Value v = method.invoke(args);
			prepareCallResult(v, settings);
			ram().add(v);
		}catch(RuntimeException e){
			ram().add(new Err(this, e));
		}
	}

	private void prepareCallResult(Value v, ArrayList<Value> settings) throws Exception {
		for(int i = 0; i < settings.size(); i++){
			settings.get(i).invoke(v);
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
	public void load(String var, Scope sc) {
		ram().add(sc.load(var));
	}

	@Override
	public void put(Value v) {
		ram().add(v);
	}

	@Override
	public void a(int size) {
		put(new ArrayValue(this, size));
	}

	@Override
	public void reserve(String var, Scope sc) {
		put(sc.reserve(var));
	}
	@Override
	public void sPut(String funcName) {
		stack().push(funcName);
	}
	@Override
	public String sPop() {
		return stack().pop();
	}
	@Override
	public int sSize() {
		return stack().size();
	}
	public boolean isDebugState() {
		return debugState;
	}
	public void setDebugState(boolean debugState) {
		this.debugState = debugState;
	}

}
