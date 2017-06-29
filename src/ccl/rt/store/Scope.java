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
		variables.put("add", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.add(vm, args[0], args[1]);
			}
		});
		variables.put("sub", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.sub(vm, args[0], args[1]);
			}
		});
		variables.put("mul", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.mul(vm, args[0], args[1]);
			}
		});
		variables.put("div", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.div(vm, args[0], args[1]);
			}
		});
		variables.put("equals", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.equals(vm, args[0], args[1]);
			}
		});
		variables.put("concat", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.concat(vm, args[0], args[1]);
			}
		});
		variables.put("mod", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.mod(vm, args[0], args[1]);
			}
		});
		variables.put("pow", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.pow(vm, args[0], args[1]);
			}
		});
		variables.put("lss", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.lss(vm, args[0], args[1]);
			}
		});
		variables.put("gtr", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.gtr(vm, args[0], args[1]);
			}
		});
		variables.put("not", new Func(vm){
			@Override
			public Value invoke(Value... args) {
				return Std.not(vm, args[0]);
			}
		});
		variables.put("nvp", new Func(vm) {
			@Override
			public Value invoke(Value... args) {
				return Nvp.makeNvp(vm, new NvpVal(args[0], args[1]));
			}
		});
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
		reserve("java").setValue(new Func(vm){
			@Override
			public Value invoke(Value... args) {
				try {
					return Spec.java(vm, f, args[0].getValue() + "");
				} catch (ClassNotFoundException e) {
					return new Err(vm, e);
				}
			}
		});
		try{
			reserve("eval").setValue((Value) EvalSetup.reflectEvalSupport(vm));
		}catch(Exception e){}
	}
	private void initStd() {
		reserve("undefined").setValue(Expression.make(vm, Special.UNDEFINED));
		reserve("false").setValue(Expression.make(vm, false));
		reserve("true").setValue(Expression.make(vm, true));
		reserve("null").setValue(Expression.make(vm, Special.NULL));
		reserve("while").setValue(new Func(vm){

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
		reserve("if").setValue(new Func(vm){

			@Override
			public Value invoke(Value... args) {
				final Value cnd = args[0];
				return new Func(vm){

					@Override
					public Value invoke(Value... args) {
						try {
							return Std.ifGlobal(vm,args[0],args.length < 2 ? null : args[1],cnd);
						} catch (Exception e) {
							e.printStackTrace();
							return new Err(vm,e);
						}
					}
					
				};
			}
			
		});
		reserve("for").setValue(new Func(vm){

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
