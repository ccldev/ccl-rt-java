package ccl.rt;

import java.util.List;

public class ArrayComparable implements Comparable<ArrayComparable> {

	int index;
	private Value[] vs;

	public ArrayComparable(Value[] vs, int index){
		this.vs = vs;
		this.index = index;
	}
	
	@Override
	public int compareTo(ArrayComparable arg0) {
		return ((Comparable) vs[index].getValue()).compareTo((Comparable) vs[arg0.index].getValue());
	}

}
