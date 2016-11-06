package ccl.rt.store;

import java.util.HashMap;

import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;

public class Scope {
	
	HashMap<String, Value> variables;
	private Scope parent;
	
	{
		variables = new HashMap<String, Value>();
	}
	
	public Scope(){}
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
	
	public void reserve(String name) {
		variables.put(name, new Expression(Special.UNDEFINED));
	}
	
}
