package ccl.rt;

import java.util.ArrayList;
import java.util.Arrays;

public class Array {
	
	private ArrayList<Value> base;
	
	{
		base = new ArrayList<Value>();
	}
	
	public Array(int size){
		for(int i = 0; i < size; i++){
			base.add(new Expression(Special.UNDEFINED));
		}
	}
	
	public Array(Value[] args) {
		for(int i = 0; i < args.length; i++){
			base.add(args[i]);
		}
	}

	public Value getExpression(int index){
		return base.get(index);
	}
	
	public Object get(int index){
		return getExpression(index).getValue();
	}
	
	public int length(){
		return base.size();
	}
	
	public void pushValue(Value v){
		base.add(v);
	}
	
	public String toString(){
		Object[] arr = new Object[base.size()];
		for(int i = 0; i < arr.length; i++){
			arr[i] = get(i);
		}
		return Arrays.toString(arr);
	}
	
}
