package ccl.rt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ccl.rt.err.Err;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.data.*;
import io.github.coalangsoft.lib.sequence.ModifiableSequence;
import io.github.coalangsoft.lib.sequence.SequenceTool;
import io.github.coalangsoft.lib.sequence.basic.BasicModifiableSequence;

public class Array extends ModifiableSequence<Value,Array> {

	private io.github.coalangsoft.lib.data.Func<Integer,Value> factory;
	private IVM vm;
	private int specLength = Integer.MAX_VALUE;

	public Array(IVM vm, Value[] values){
		super(new SequenceTool<>((p) -> new Array(vm, p), (p) -> new Value[p]), values);
	}

	public Array(IVM vm){
		super(new SequenceTool<>((p) -> new Array(vm, p), (p) -> new Value[p]), new Value[0]);
		values = null;
	}

	public Array(IVM vm, int size){
		this(vm,new Value[0]);
		this.vm = vm;
		for(int i = 0; i < size; i++){
			add(Expression.make(vm, Special.UNDEFINED));
		}
	}
	
	public Array(IVM vm, io.github.coalangsoft.lib.data.Func<Integer,Value> factory){
		this(vm);
		this.vm = vm;
		this.factory = factory;
	}
	public Array(IVM vm, io.github.coalangsoft.lib.data.Func<Integer,Value> factory, int size){
		this(vm);
		this.vm = vm;
		this.factory = factory;
		specLength = size;
	}

	public Value getExpression(int index){
		if(values == null){
			return factory.call(index);
		}
		return at(index);
	}
	
	public Object get(int index){
		return getExpression(index).getValue();
	}
	
	public int length(){
		if(values != null){
			return values.length;
		}else{
			return specLength;
		}
	}
	
	public void pushValue(Value v){
		if(values == null){
			throw new UnsupportedOperationException("Can not add Value to dynamic array!");
		}
		add(v);
	}
	
	public Array cut(int skip){
		Array ret = new Array(vm, 0);
		for(int i = skip; i < length(); i++){
			ret.pushValue(getExpression(i));
		}
		return ret;
	}
	
	public Value operate(String op){
		if(length() == 0){
			return Expression.make(vm, Special.UNDEFINED);
		}
		Value v = getExpression(0);
		for(int i = 1; i < length(); i++){
			try {
				v = v.getProperty(false, op).invoke(getExpression(i));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return v;
	}
	
	public Value setValue(int index, Value v){
		if(values == null){
			throw new UnsupportedOperationException("Can not set Value on dynamic array!");
		}
		return set(index,v);
	}
	
	public Value remove(int index){
		if(values == null){
			throw new UnsupportedOperationException("Can not remove Value on dynamic array!");
		}
		return super.remove(index);
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
	
	public Value repeat(){
		
		if(values == null){
			return new ArrayValue(vm, this);
		}
		
		final int origLength = length();
		final Value[] origArray = values.clone();
		
		return new ArrayValue(vm, new Array(vm, 
				(p) -> origArray[p % origLength]
		));
	}
	
	public Value sort0(){
		if(values == null){
			throw new UnsupportedOperationException("Unable to sort dynamic array!");
		}
		
		int length = length();
		ArrayList<ArrayComparable> cs = new ArrayList<ArrayComparable>();
		for(int i = 0; i < length; i++){
			cs.add(new ArrayComparable(values,i));
		}
		Collections.sort(cs);
		
		Array a = new Array(vm,0);
		for(int i = 0; i < length; i++){
			a.pushValue(values[cs.get(i).index]);
		}
		return new ArrayValue(vm, a);
	}
	
	public Value zip(Value condition) throws Exception{
		Array a = (Array) get(0);
		Array b = (Array) get(1);
		Array c = new Array(vm,0);
		for(int i = 0; i < a.length(); i++){
			c.pushValue(condition.invoke(
				a.getExpression(i),
				b.getExpression(i)
			));
		}
		return new ArrayValue(vm,c);
	}
	
	public int indexOf(Object o){
		for(int i = 0; i < length(); i++){
			if(o.equals(get(i))){
				return i;
			}
		}
		return -1;
	}

	public String toString(){
		ArrayList<Object> vals = new ArrayList<>();
		for(int i = 0; i < length(); i++){
			vals.add(get(i));
		}
		return vals.toString();
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
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneBoolean(IVM vm, boolean[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneByte(IVM vm, byte[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneShort(IVM vm, short[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneInt(IVM vm, int[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneLong(IVM vm, long[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneFloat(IVM vm, float[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
	private static Array cloneDouble(IVM vm, double[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}

	private static <T> Array clone0(IVM vm, T[] o) {
		Array ret = new Array(vm, 0);
		for(int i = 0; i < o.length; i++){
			ret.pushValue(Expression.make(vm, o[i]));
		}
		return ret;
	}
	
}
