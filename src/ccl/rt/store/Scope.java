package ccl.rt.store;

import io.github.coalangsoft.lib.reflect.CustomClassFinder;

import java.util.HashMap;

import coa.scripting.EvalSetup;

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
import ccl.rt.vm.ScopeVal;

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
	}
	
	private void initCasters() {
		variables.put("boolean", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.boolean_(vm, args[0]);
			}
		});
		variables.put("error", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.error(vm, args[0]);
			}
		});
		variables.put("float", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.float_(vm, args[0]);
			}
		});
		variables.put("integer", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.integer(vm, args[0]);
			}
		});
		variables.put("char2int", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.char2int(vm, args[0]);
			}
		});
		variables.put("array", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.array(vm, args[0]);
			}
		});
		variables.put("char", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.char_(vm, args[0]);
			}
		});
		variables.put("byte", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.byte_(vm, args[0]);
			}
		});
		variables.put("regex", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Environment.regex(vm, args[0]);
			}
		});
		variables.put("unbound", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return new Unbound(args[0]);
			}
		});
	}
	private void initSpec(final CustomClassFinder f) {
		load("java").setValue(new Func(vm){
			@Override
			public Value invoke(Value... args) {
				try {
					return Spec.java(vm, f, args[0].getValue() + "");
				} catch (ClassNotFoundException e) {
					return new Err(vm, e);
				}
			}
		});
		load("scope").setValue(new ScopeVal(vm, this));
		try{
			load("eval").setValue((Value) EvalSetup.reflectEvalSupport(vm));
		}catch(Exception e){}
	}
	private void initStd() {
		load("false").setValue(new Expression(vm, false));
		load("true").setValue(new Expression(vm, true));
		load("while").setValue(new Func(vm){

			@Override
			public Value invoke(Value... args) {
				final Value cnd = args[0];
				return new Func(vm){

					@Override
					public Value invoke(Value... args) {
						try {
							return Std.whileGlobal(vm,args[0],cnd);
						} catch (Exception e) {
							return new Err(vm,e);
						}
					}
					
				};
			}
			
		});
		load("for").setValue(new Func(vm){

			@Override
			public Value invoke(Value... args) {
				final Value[] cnd = args;
				return new Func(vm){

					@Override
					public Value invoke(Value... args) {
						try {
							return Std.forGlobal(vm,args[0], cnd);
						} catch (Exception e) {
							return new Err(vm,e);
						}
					}
					
				};
			}
			
		});
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
				self.reserve(name);
				return self;
			}else{
				return parent().load0(self, name);
			}
		}else{
			return this;
		}
	}
	
	public Variable reserve(String name) {
		variables.put(name, new Expression(vm, Special.UNDEFINED));
		return load(name);
	}
	
}
