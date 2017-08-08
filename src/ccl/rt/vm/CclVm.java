package ccl.rt.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import ccl.rt.Array;
import ccl.rt.ArrayValue;
import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Unbound;
import ccl.rt.Value;
import ccl.rt.store.Scope;
import ccl.rt.v6.PreparedCoaFunction;
import coa.rt.Nvp;
import cpa.subos.io.IOBase;
import cpa.subos.io.file.FileIOBase;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
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
		functionMap = new HashMap<>();
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
		ram().add(Expression.make(this, StringPrepare.prepare(string)));
	}

	@Override
	public void f(String floatn) {
		ram().add(Expression.make(this, Double.parseDouble(floatn)));
	}

	@Override
	public void i(String integer) {
		ram().add(Expression.make(this, Double.parseDouble(integer)));
	}

	private static final String UNKNOWN = "UNKNOWN SOURCE";

	private HashMap<String, PreparedCoaFunction> functionMap;

	@Override
	public void m(final Runner r, final io.github.coalangsoft.lib.data.Func<Void, IOBase<?>> f, final Scope sc) {
		IOBase<?> iobase = f.call(null);
		String path = iobase instanceof FileIOBase ? ((FileIOBase) iobase).getPath() : UNKNOWN;

		final Runner runner = r.create();
		runner.creation(iobase);

		final PreparedCoaFunction fu = new PreparedCoaFunction(this, iobase, r);
		Func res = new Func(this){

			@Override
			public Value invoke(Value... args) {
				ArrayValue arr = new ArrayValue(CclVm.this, new Array(CclVm.this, args));
				
				Scope myScope = sc.chain();
				myScope.reserve("@");
				myScope.load("@").setValue(arr);
				ArrayList<Value> oldRam = ram();

				sPut(path);
				setRam(new ArrayList<Value>());
				Value v = fu.invoke(myScope);
				setRam(oldRam);
				sPop();

				return v;
			}
			
		};
		
		put(res);
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
			if(v instanceof Nvp){
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
			StackTraceFormer.formException(e, CclVm.this).printStackTrace();
			throw StackTraceFormer.formException(e, CclVm.this);
		}
	}

	public void swapAndCall(int paramCount) throws Exception {
		Value a = pop();
		Value b = pop();
		put(a);
		put(b);
		call(paramCount);
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
	public void a() {
		put(new ArrayValue(this, 0));
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
