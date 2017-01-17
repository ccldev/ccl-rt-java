package ccl.jrt;

import java.util.ArrayList;

import coa.std.NVP;
import coa.std.NVPValue;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.vm.IVM;
import ccl.jrt.JMethod;

public class J {

	public static Value invoke(IVM vm, Object o, ICallable[] methods, Value[] args) {
		ArrayList<Exception> errs = new ArrayList<Exception>();
		for(int i = 0; i < methods.length; i++){
			ICallable m = methods[i];
			if(m.getParameterCount() != args.length){
				continue;
			}
			try {
				return new JMethod(vm, o, m).call(args);
			} catch (Exception e) {
				errs.add(e);
			}
		}
		return new Err(vm, errs);
	}

	public static Value[] pack(IVM vm, Object[] arr){
		Value[] ret = new Value[arr.length];
		for(int i = 0; i < arr.length; i++){
			ret[i] = new Expression(vm, arr[i]);
		}
		return ret;
	}

}
