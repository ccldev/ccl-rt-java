package ccl.rt;

import java.util.ArrayList;
import java.util.Arrays;

import ccl.rt.err.Err;

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
		try{
			return base.get(index);
		}catch(IndexOutOfBoundsException e){
			return new Err(e);
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
		Array ret = new Array(0);
		for(int i = skip; i < length(); i++){
			ret.pushValue(getExpression(i));
		}
		return ret;
	}
	
	public String toString(){
		Object[] arr = new Object[base.size()];
		for(int i = 0; i < arr.length; i++){
			arr[i] = get(i);
		}
		return Arrays.toString(arr);
	}
	
	public static Array clone(Object o){
		if(o instanceof Object[]){
			return clone0((Object[]) o);
		}
		if(o instanceof char[]){
			return cloneChar((char[]) o);
		}
		if(o instanceof boolean[]){
			return cloneBoolean((boolean[]) o);
		}
		if(o instanceof byte[]){
			return cloneByte((byte[]) o);
		}
		if(o instanceof short[]){
			return cloneShort((short[]) o);
		}
		if(o instanceof int[]){
			return cloneInt((int[]) o);
		}
		if(o instanceof long[]){
			return cloneLong((long[]) o);
		}
		if(o instanceof float[]){
			return cloneFloat((float[]) o);
		}
		if(o instanceof double[]){
			return cloneDouble((double[]) o);
		}
		throw new RuntimeException(o.getClass() + "");
	}

	private static Array cloneChar(char[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneBoolean(boolean[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneByte(byte[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneShort(short[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneInt(int[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneLong(long[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneFloat(float[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
	private static Array cloneDouble(double[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}

	private static <T> Array clone0(T[] o) {
		Array ret = new Array(0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(new Expression(o[i]));
		}
		return ret;
	}
	
}
