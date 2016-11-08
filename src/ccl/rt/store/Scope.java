package ccl.rt.store;

import java.util.HashMap;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.lib.Environment;

public class Scope {
	
	HashMap<String, Value> variables;
	private Scope parent;
	
	{
		variables = new HashMap<String, Value>();
	}
	
	public Scope(){
		initGlobals();
	}
	private void initGlobals() {
		variables.put("boolean", new Func(){
			@Override
			public Value invoke(Value... args) {
				return Environment.boolean_(args[0]);
			}
		});
		variables.put("error", new Func(){
			@Override
			public Value invoke(Value... args) {
				return Environment.error(args[0]);
			}
		});
		variables.put("float", new Func(){
			@Override
			public Value invoke(Value... args) {
				return Environment.float_(args[0]);
			}
		});
		variables.put("integer", new Func(){
			@Override
			public Value invoke(Value... args) {
				return Environment.integer(args[0]);
			}
		});
		variables.put("array", new Func(){
			@Override
			public Value invoke(Value... args) {
				return Environment.array(args[0]);
			}
		});
	}
	private Scope(Scope parent){
		this.parent = parent;
	}
	
	public Scope chain(){
		return new Scope(this);
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
		variables.put(name, new Expression(Special.UNDEFINED));
		return load(name);
	}
	
}
