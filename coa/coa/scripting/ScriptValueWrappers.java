package coa.scripting;

import io.github.coalangsoft.lib.data.Func;

import java.util.HashMap;

import javax.script.ScriptEngine;

import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class ScriptValueWrappers {

	static{
		map = new HashMap<Class<? extends ScriptEngine>, Func<Object, Value>>();
	}
	
	private static HashMap<Class<? extends ScriptEngine>, Func<Object,Value>> map;
	
	public static Func<Object,Value> find(ScriptEngine engine) {
		return map.get(engine.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public static void init(IVM vm){
		try {
			map.put((Class<? extends ScriptEngine>) Class.forName("jdk.nashorn.api.scripting.NashornScriptEngine"), (Func<Object, Value>) Class.forName("coa.scripting.support.nashorn.NashornValueWrapper").getConstructor(IVM.class).newInstance(vm));
		} catch (Exception e) {}
	}

}
