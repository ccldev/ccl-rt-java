package coa.rt.scripting.support.nashorn;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import ccl.rt.Array;
import ccl.rt.vm.IVM;

public class NashornArray extends Array {

	public NashornArray(IVM vm, ScriptObjectMirror m) {
		super(vm, (int) m.getMember("length"));
		for(int i = 0; i < length(); i++){
			setValue(i, NashornValueWrapper.INSTANCE.call(m.getSlot(i)));
		}
	}

}
