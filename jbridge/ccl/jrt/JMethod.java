package ccl.jrt;

import io.github.coalangsoft.reflect.Clss;

import java.lang.reflect.Proxy;

import ccl.rt.Array;
import ccl.rt.Func;
import ccl.rt.Value;
import ccl.rt.Expression;
import ccl.rt.vm.IVM;

public class JMethod {

	private ICallable method;
	private Object object;
	private IVM vm;

	public JMethod(IVM vm, Object o, ICallable m) {
		this.vm = vm;
		this.method = m;
		this.object = o;
	}

	public Value call(Value[] args) throws Exception {
		Clss[] ptypes = method.getParameterTypes().getRaw();
		Object[] arr = new Object[args.length];
		for(int i = 0; i < ptypes.length; i++){
			if(ptypes[i].isPrimitive()){
				castPrimitive(i, arr, ptypes[i].getSimpleName(), args[i].getValue());
			}else if(ptypes[i].isArray() && args[i].getValue() instanceof Array){
				arr[i] = JArray.cast(ptypes[i].base, (Array) args[i].getValue());
			}else if(ptypes[i].base == Boolean.class && args[i].getValue() instanceof Number){
				arr[i] = ((Number)args[i].getValue()).intValue() == 1;
			}else{
				if(ptypes[i].isInterface()){
					if(!ptypes[i].isInstance(args[i].getValue()) && args[i] instanceof Func){
						if(ptypes[i].method(null).listSpecific(object, true).length() == 1){
							Class<?> iface = ptypes[i].base;
							String name = iface.getDeclaredMethods()[0].getName();
							arr[i] = Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, new SingleInvocationHandler(vm, name, args[i]));
							continue;
						}
					}
				}
				arr[i] = ptypes[i].base.cast(args[i].getValue());
			}
		}
		return new Expression(vm, method.invoke(object, (Object[]) arr));
		
	}

	private void castPrimitive(int i, Object[] arr, String type,
			Object value) throws Exception {
		switch(type){
		case "byte": arr[i] = ((Number) value).byteValue(); break;
		case "short": arr[i] = ((Number) value).shortValue(); break;
		case "int": arr[i] = ((Number) value).intValue(); break;
		case "long": arr[i] = ((Number) value).longValue(); break;
		
		case "float": arr[i] = ((Number) value).floatValue(); break;
		case "double": arr[i] = ((Number) value).doubleValue(); break;
		
		case "char": arr[i] = ((Character) value).charValue(); break;
		case "boolean": arr[i] = ((Number) value).intValue() == 1; break;
		
		default: throw new Exception("Unknown primitive: " + type);
		}
	}

}
