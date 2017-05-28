package ccl.rt.vm;

import java.util.ArrayList;
import java.util.Stack;

public class StackTraceFormer {
	
	public static Exception formException(Exception msg, IVM vm){
		Exception e = new Exception(msg);
		
		ArrayList<StackTraceElement> list = new ArrayList<StackTraceElement>();
		Stack<String> clone = new Stack<String>();
		
		while(vm.sSize() > 0){
			String s = vm.sPop();
			clone.push(s);
			list.add(new StackTraceElement("CCLRuntime", "debug", s, 0));
		}
		
		while(clone.size() > 0){
			vm.sPut(clone.pop());
		}
		
		e.setStackTrace(list.toArray(new StackTraceElement[0]));
		return e;
	}
	
}
