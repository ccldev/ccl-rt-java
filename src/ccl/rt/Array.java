package ccl.rt;

import java.util.ArrayList;
import java.util.Arrays;

import ccl.rt.err.Err;
import ccl.rt.vm.IVM;

public class Array {
	
	private ArrayList<Value> base;
	private IVM vm;
	
	{
		base = new ArrayList<Value>();
	}
	
	public Array(IVM vm, int size){
		this.vm = vm;
		for(int i = 0; i < size; i++){
			base.add(new Expression(vm, Special.UNDEFINED));
		}
	}
	
	public Array(IVM vm, Value[] args) {
		this.vm = vm;
		for(int i = 0; i < args.length; i++){
			base.add(args[i]);
		}
	}

	public Value getExpression(int index){
		try{
			return base.get(index);
		}catch(IndexOutOfBoundsException e){
			return new Err(vm, e);
		}
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
	
	public Array cut(int skip){
		Array ret = new Array(vm, 0);
		for(int i = skip; i < length(); i++){
			ret.pushValue(getExpression(i));
		}
		return ret;
	}
	
	public Value operate(String op){
		if(base.size() == 0){
			return new Expression(vm, Special.UNDEFINED);
		}
		Value v = base.get(0);
		for(int i = 1; i < base.size(); i++){
			try {
				v = v.getProperty(op).invoke(base.get(i));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return v;
	}
	
	public Value setValue(int index, Value v){
		return base.set(index,v);
	}
	
	public Value remove(int index){
		return base.remove(index);
	}
	
	public Value select(Value condition) throws Exception{
		Array a = new Array(vm,new Value[0]);
		
		for(int i = 0; i < length(); i++){
			if(((Number)condition.invoke(getExpression(i)).getValue()).intValue() == 1){
				a.pushValue(getExpression(i));
			}
		}
		
		return new ArrayValue(vm,a);
	}
	
	public String toString(){
		Object[] arr = new Object[base.size()];
		for(int i = 0; i < arr.length; i++){
			arr[i] = get(i);
		}
		return Arrays.toString(arr);
	}
	
	public static Array clone(IVM vm, Object o){
		if(o instanceof Object[]){
			return clone0(vm, (Object[]) o);
		}
		if(o instanceof char[]){
			return cloneChar(vm, (char[]) o);
		}
		if(o instanceof boolean[]){
			return cloneBoolean(vm, (boolean[]) o);
		}
		if(o instanceof byte[]){
			return cloneByte(vm, (byte[]) o);
		}
		if(o instanceof short[]){
			return cloneShort(vm, (short[]) o);
		}
		if(o instanceof int[]){
			return cloneInt(vm, (int[]) o);
		}
		if(o instanceof long[]){
			return cloneLong(vm, (long[]) o);
		}
		if(o instanceof float[]){
			return cloneFloat(vm, (float[]) o);
		}
		if(o instanceof double[]){
			return cloneDouble(vm, (double[]) o);
		}
		throw new RuntimeException(o.getClass() + "");
	}

	private static Array cloneChar(IVM vm, char[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneBoolean(IVM vm, boolean[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneByte(IVM vm, byte[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneShort(IVM vm, short[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneInt(IVM vm, int[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneLong(IVM vm, long[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneFloat(IVM vm, float[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneDouble(IVM vm, double[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}

	private static <T> Array clone0(IVM vm, T[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(vm, o[i]));
		}
		return ret;
	}
	
}
