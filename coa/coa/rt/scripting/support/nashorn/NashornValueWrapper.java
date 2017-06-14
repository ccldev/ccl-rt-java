package coa.rt.scripting.support.nashorn;

import io.github.coalangsoft.lib.data.Func;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class NashornValueWrapper implements Func<Object, Value> {

	public static NashornValueWrapper INSTANCE;
	private IVM vm;
	
	public NashornValueWrapper(IVM vm){
		this.vm = vm;
		INSTANCE = this;
	}
	
	@Override
	public Value call(Object p) {
		if(p instanceof ScriptObjectMirror){
			ScriptObjectMirror m = (ScriptObjectMirror) p;
			if(m.isArray()){
				return new NashornArrayValue(vm, m);
			}
			if(ScriptObjectMirror.isUndefined(m)){
				return Expression.make(vm, Special.UNDEFINED);
			}
			return new NashornMirrorValue(vm, m);
		}
		return Expression.make(vm, p);
	}

}
