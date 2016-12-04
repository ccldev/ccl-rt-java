package ccl.rt.thread;

import ccl.rt.Expression;
import ccl.rt.Func;
import ccl.rt.Special;
import ccl.rt.Value;

public class ThreadDataExpression extends Expression {

	private ThreadData data;

	public ThreadDataExpression(Thread t) {
		super(Special.INVALID);
		this.data = new ThreadData(t);
		setValue(data);
	}
	
	public Value getProperty(String p){
		synchronized (this) {
			if(p.equals("write")){
				return new Func(){
					@Override
					public Value invoke(Value... args) {
						data.getShared().write(
							args[0].getValue().toString(),
							args[1].getValue().toString(),
							args[2]
						);
						return new Expression(Special.UNDEFINED);
					}
				};
			}else if(p.equals("read")){
				return new Func(){
					@Override
					public Value invoke(Value... args) {
						return data.getShared().read(
							args[0].getValue().toString()
						);
					}
				};
			}else{
				return super.getProperty(p);
			}
		}
	}

}
