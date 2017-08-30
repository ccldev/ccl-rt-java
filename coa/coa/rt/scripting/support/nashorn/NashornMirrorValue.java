package coa.rt.scripting.support.nashorn;

import io.github.coalangsoft.lib.data.Func;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;

public class NashornMirrorValue extends Expression {

	private ScriptObjectMirror mirror;
	private IVM vm;

	public NashornMirrorValue(IVM vm, ScriptObjectMirror value) {
		super(vm, new Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return value;
			}
		});
		this.vm = vm;
		this.mirror = value;
	}
	
	public Value invoke(Value... args) throws Exception{
		if(mirror.isFunction()){
			Object[] params = new Object[args.length];
			for(int i = 0; i < args.length; i++){
				params[i] = args[i].getValue();
			}
			return wrap(mirror.call(null, params));
		}else{
			return super.invoke(args);
		}
	}
	
	public Value getProperty0(boolean asPrototype, String name){
		if(mirror.hasMember(name)){
			return wrap(mirror.getMember(name));
		}else{
			return super.getProperty0(asPrototype, name);
		}
	}

	private Value wrap(Object slot) {
		return NashornValueWrapper.INSTANCE.call(slot);
	}

}
