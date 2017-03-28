package ccl.rt;

import java.util.List;

public class ArrayComparable implements Comparable<ArrayComparable> {

	int index;
	private List<Value> vs;

	public ArrayComparable(List<Value> vs, int index){
		this.vs = vs;
		this.index = index;
	}
	
	@Override
	public int compareTo(ArrayComparable arg0) {
		return ((Comparable) vs.get(index).getValue()).compareTo((Comparable) vs.get(arg0.index).getValue());
	}

}
