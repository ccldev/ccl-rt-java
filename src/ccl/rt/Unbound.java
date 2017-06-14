package ccl.rt;

import io.github.coalangsoft.lib.data.Func;

import ccl.rt.unbound.UnboundGetFunc;
import ccl.rt.unbound.UnboundInvokeFunc;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;

import java.util.List;

public class Unbound implements Value {
	
	private Func<Value,Value>[] funcList;

	public Unbound(Value str) {
		funcList = new Func[]{
			new UnboundGetFunc(str.getValue().toString())
		};
	}

	public Unbound(Func<Value, Value>[] cpy) {
		this.funcList = cpy;
	}

	@Override
	public Value getProperty(String name) {
		Func<Value,Value>[] cpy = new Func[funcList.length + 1];
		for(int i = 0; i < funcList.length; i++){
			cpy[i] = funcList[i];
		}
		cpy[cpy.length - 1] = new UnboundGetFunc(name);
		return new Unbound(cpy);
	}

	@Override
	public Object getValue() {
		return this;
	}

	@Override
	public List<String> getProperties() {
		throw new RuntimeException("NI");
	}

	@Override
	public Value invoke(Value... args) throws Exception {
		Func<Value,Value>[] cpy = new Func[funcList.length + 1];
		for(int i = 0; i < funcList.length; i++){
			cpy[i] = funcList[i];
		}
		cpy[cpy.length - 1] = new UnboundInvokeFunc(args);
		return new Unbound(cpy);
	}

	@Override
	public DynamicBoolean bool() {
		throw new RuntimeException("Not allowed to use an unbound as boolean!");
	}

	@Override
	public DynamicDouble num() {
		throw new RuntimeException("Not allowed to use an unbound as number!");
	}

	public ccl.rt.Func formFunction(IVM vm) {
		final Func<Value,Value>[] fs = new Func[funcList.length];
		for(int i = 0; i < fs.length; i++){
			fs[i] = funcList[i];
		}
		
		return new ccl.rt.Func(vm){

			@Override
			public Value invoke(Value... args) {
				Value res = args[0];
				for(int i = 0; i < fs.length; i++){
					res = fs[i].call(res);
				}
				return res;
			}
			
		};
	}

}
