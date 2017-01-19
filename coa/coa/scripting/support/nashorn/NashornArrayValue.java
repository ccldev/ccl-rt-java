package coa.scripting.support.nashorn;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import ccl.rt.ArrayValue;
import ccl.rt.vm.IVM;

public class NashornArrayValue extends ArrayValue {

	public NashornArrayValue(IVM vm, ScriptObjectMirror value) {
		super(vm, new NashornArray(vm, value));
	}

}
