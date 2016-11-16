package ccl.jrt;

import ccl.rt.Array;
import ccl.rt.Value;
import ccl.rt.Expression;

public class JMethod {

	private ICallable method;
	private Object object;

	public JMethod(Object o, ICallable m) {
		this.method = m;
		this.object = o;
	}

	public Value call(Value[] args) throws Exception {
		Class<?>[] ptypes = method.getParameterTypes();
		Object[] arr = new Object[args.length];
		for(int i = 0; i < ptypes.length; i++){
			if(ptypes[i].isPrimitive()){
				castPrimitive(i, arr, ptypes[i].getSimpleName(), args[i].getValue());
			}else if(ptypes[i].isArray() && args[i].getValue() instanceof Array){
				arr[i] = JArray.cast(ptypes[i], (Array) args[i].getValue());
			}else{
				arr[i] = ptypes[i].cast(args[i].getValue());
			}
		}
		return new Expression(method.invoke(object, (Object[]) arr));
		
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
		case "boolean": arr[i] = ((Boolean) value).booleanValue(); break;
		
		default: throw new Exception("Unknown primitive: " + type);
		}
	}

}
