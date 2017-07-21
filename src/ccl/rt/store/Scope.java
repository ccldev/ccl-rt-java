package ccl.rt.store;

import coa.rt.Nvp;
import coa.rt.NvpVal;
import io.github.coalangsoft.lib.reflect.CustomClassFinder;

import java.util.HashMap;

import coa.rt.scripting.EvalSetup;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Unbound;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.lib.Environment;
import ccl.rt.lib.Spec;
import ccl.rt.lib.Std;
import ccl.rt.thread.ThreadDataExpression;
import ccl.rt.vm.IVM;

public class Scope {
	
	HashMap<String, Value> variables;
	private Scope parent;
	private IVM vm;
	
	
	public Scope(IVM vm, CustomClassFinder f){
		variables = new HashMap<String, Value>();
		variables.put("thread",new ThreadDataExpression(vm, Thread.currentThread()));
		this.vm = vm;
		initCasters();
		initStd();
		initSpec(f);
		initOp();
	}

	private void initOp() {
		variables.put("add", Func.by(vm, Std::add));
		variables.put("sub", Func.by(vm, Std::sub));
		variables.put("mul", Func.by(vm, Std::mul));
		variables.put("div", Func.by(vm, Std::div));
		variables.put("equals", Func.by(vm, (args) -> Std.equals(vm, args[0], args[1])));
		variables.put("concat", Func.by(vm, Std::concat));
		variables.put("mod", Func.by(vm, Std::mod));
		variables.put("pow", Func.by(vm, Std::pow));
		variables.put("lss", Func.by(vm, Std::lss));
		variables.put("gtr", Func.by(vm, Std::gtr));
		variables.put("not", Func.by(vm, Std::not));
		variables.put("nvp", Func.by(vm, (args) -> Nvp.makeNvp(vm, new NvpVal(args[0], args[1]))));
	}

	private void initCasters() {
		variables.put("boolean", Func.by(vm, Environment::boolean_));
		variables.put("error", Func.by(vm, Environment::error));
		variables.put("float", Func.by(vm, Environment::float_));
		variables.put("integer", Func.by(vm, Environment::integer));
		variables.put("char2int", Func.by(vm, Environment::char2int));
		variables.put("array", Func.by(vm, Environment::array));
		variables.put("char", Func.by(vm, Environment::char_));
		variables.put("byte", Func.by(vm, Environment::byte_));
		variables.put("regex", Func.by(vm, Environment::regex));
//		variables.put("unbound", new Func(vm){
//			@Override
//			public Value invoke(Value... args) {
//				return new Unbound(args[0]);
//			}
//		});
		variables.put("println", Func.by(vm, (args) -> {
			if(args.length == 0){
				System.out.println();
			}else{
				System.out.println(args[0].getValue());
			}
			return Expression.make(vm, Special.UNDEFINED);
		}));
	}
	private void initSpec(final CustomClassFinder f) {
		reserve("java").setValue(Func.by(vm, (args) -> {
			try {
				return Spec.java(vm, f, args[0].getValue() + "");
			} catch (ClassNotFoundException e) {
				return new Err(vm, e);
			}
		}));
		try{
			reserve("eval").setValue((Value) EvalSetup.reflectEvalSupport(vm));
		}catch(Exception e){}
	}
	private void initStd() {
		reserve("undefined").setValue(Expression.make(vm, Special.UNDEFINED));
		reserve("false").setValue(Expression.make(vm, false));
		reserve("true").setValue(Expression.make(vm, true));
		reserve("null").setValue(Expression.make(vm, Special.NULL));
		reserve("while").setValue(Func.by(vm,(args) ->{
			final Value cnd = args[0];
			return Func.by(vm, (a) -> {
				try {
					return Std.whileGlobal(vm,a[0],cnd);
				} catch (Exception e) {
					return new Err(vm,e);
				}
			});
		}));
		reserve("if").setValue(Func.by(vm, (args) -> {
			final Value cnd = args[0];
			return Func.by(vm, (a) -> {
				try {
					return Std.ifGlobal(vm, a[0], a.length < 2 ? null : a[1], cnd);
				} catch (Exception e) {
					e.printStackTrace();
					return new Err(vm, e);
				}
			});
		}));
		reserve("for").setValue(Func.by(vm, (args) -> {
			final Value[] cnd = args;
			return Func.by(vm, (a) -> {
				try {
					return Std.forGlobal(vm,a[0], cnd);
				} catch (Exception e) {
					return new Err(vm,e);
				}
			});
		}));
		reserve("map").setValue(Func.by(vm, (args) -> Std.map(vm)));
	}
	
	private Scope(IVM vm, Scope parent){
		this.vm = vm;
		variables = new HashMap<String, Value>();
		variables.put("thread",new ThreadDataExpression(vm, Thread.currentThread()));
		this.parent = parent;
	}
	
	public Scope chain(){
		return new Scope(vm, this);
	}
	
	public Scope parent(){
		if(parent == null){
			throw new RuntimeException("No parent scope!");
		}
		return parent;
	}
	
	public boolean isGlobal(){
		return parent == null;
	}
	
	public Variable load(String name){
		Scope s = load0(this, name);
		return new Variable(s, name);
	}
	
	/**
	 * Loads a Value from the current Scope.
	 * ONLY uses the current Scope for search.
	 * To find values in the current Scope and
	 * its parents, use {@link #load(String)}
	 * @param name a name
	 * @return the Value (if found) or null.
	 */
	Value loadDirect(String name){
		return variables.get(name);
	}
	
	/**
	 * Searches for a variable. If the variable was
	 * found in this or a parent scope, it returns
	 * the Scope that contains the Variable.
	 * If the variable does not exist, it is created
	 * in this Scope.
	 * @param name the Variable Name.
	 * @return The Scope that contains the Variable.
	 */
	private Scope load0(Scope self, String name){
		Value v = variables.get(name);
		if(v == null){
			if(isGlobal()){
				throw new RuntimeException("Variable not found: " + name);
			}else{
				return parent().load0(self, name);
			}
		}else{
			return this;
		}
	}
	
	public Variable reserve(String name) {
		variables.put(name, Expression.make(vm, Special.UNDEFINED));
		return load(name);
	}
	
}
