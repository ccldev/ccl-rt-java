package coa.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class EvalSupport extends Expression{
	
	private ScriptEngineManager mgr;
	private IVM vm;

	public EvalSupport(IVM vm, ScriptEngineManager mgr){
		super(vm, mgr);
		ScriptValueWrappers.init(vm);
		this.vm = vm;
		this.mgr = mgr;
	}
	
	@Override
	public Value getProperty(String name) {
		ScriptEngine e = mgr.getEngineByExtension(name);
		if(e != null){
			return new ScriptEngineValue(vm,
				e,
				ScriptValueWrappers.find(e)
			);
		}
		return super.getProperty(name);
	}

}
