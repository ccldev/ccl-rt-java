package ccl.rt.use;

import ccl.rt.vm.StackTraceFormer;
import ccl.v2_1.err.DebugException;
import cpa.subos.io.IOBase;
import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.lib.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Scanner;

import ccl.rt.Special;
import ccl.rt.Tool;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.store.Scope;
import ccl.rt.store.Variable;
import ccl.rt.vm.IVM;
import ccl.rt.vm.Runner;
import ccl.rt.Expression;

public class MnemoRunner implements Runner {

	private Scope sc;
	
	private Func<String, Func<Void,IOBase<?>>> streamMaker;
	
	private ArrayList<String> instructions;
	private ArrayList<String> arguments;
	
	public MnemoRunner(Func<String, Func<Void, IOBase<?>>> function){
		this.streamMaker = function;
		
		instructions = new ArrayList<String>();
		arguments = new ArrayList<String>();
	}
	
	@Override
	public void creation(IOBase<?> cclCode){
		Scanner s = null;
		try {
			s = new Scanner(cclCode.reader());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(s.hasNextLine()){
			creation(s.nextLine().split(" "));
		}
		s.close();
	}
	
	@Override
	public Value execute(IVM vm, Scope sc) {
		this.sc = sc;
		if(vm.isDebugState()){
			Logger.std.log("-Execute Start! Runner snapshot: " + this);
		}
		
		for(int i = 0; i < instructions.size(); i++){
			try {
				Value v = execution(instructions.get(i), arguments.get(i), vm);
				if(v != null){
					return v;
				}
			} catch (Exception e) {
				if(vm.isDebugState()){
					Logger.std.log(">Execution done! Exit state: exception (i:" + i + ")");
				}
				throw new RuntimeException(StackTraceFormer.formException(new Exception("Instruction " + i + " '" + instructions.get(i) + "'",e), vm));
			}
		}
		if(vm.isDebugState()){
			Logger.std.log(">Execution done! Exit state: normal");
		}
		return Expression.make(vm, Special.UNDEFINED);
	}
	
	private Value execution(String instr, String args, IVM vm) throws Exception{
		switch(instr){
		case "nnr":
			Value vl = vm.pop();
			if(vl != null){
				if(vl.getValue() != Special.UNDEFINED){
					return vl;
				}
			}
			break;
		case "load": vm.load(args, sc); break;
		case "putI": vm.i(args); break;
		case "invoke": invoke(args, vm); break;
		case "invoke1": invoke1(args, vm); break;
		case "store":
			Value v = vm.pop();
			((Variable) vm.pop()).setValue(v);
			break;
		case "store1":
			Variable var = (Variable) vm.pop();
			Value value = vm.pop();
			var.setValue(value);
			break;
		case "newscope": sc = vm.oScope(sc); break;
		case "oldscope": sc = vm.cScope(sc); break;
		case "putS": vm.s(args); break;
		case "putA": vm.a(); break;
		case "putM": vm.m(this.create(), streamMaker.call(args), sc); break;
		case "get": vm.put(vm.pop().getProperty(args)); break;
		case "duplicate": vm.dup(); break;
		case "pop":
			Value val = vm.pop();

			if(val instanceof Err){
				return val;
			}else if(val.getValue() instanceof Err){
				return (Err) val.getValue();
			}
			break;
		case "ret":
			Value ret = vm.pop();
			return ret;
		case "reserve":
			vm.reserve(args, sc);
			break;
//		case "sPut":
//			vm.sPut(args);
//			break;
//		case "sPop":
//			vm.sPop();
//			break;
		default: throw StackTraceFormer.formException(new Exception("Unknown instr: " + instr), vm);
		}
		return null;
	}

	private void invoke(String count, IVM vm) throws Exception {
		vm.call(Integer.parseInt(count));
	}
	private void invoke1(String count, IVM vm) throws Exception {
		vm.swapAndCall(Integer.parseInt(count));
	}

	private void creation(String[] split) {
		instructions.add(split[0]);
		arguments.add(Tool.link(1, split, " "));
	}

	@Override
	public Runner create() {
		return new MnemoRunner(streamMaker);
	}

	@Override
	public String toString() {
		return "MnemoRunner [streamMaker=" + streamMaker + /*", marks=" + marks
				+*/ ", instructions=" + instructions + ", arguments=" + arguments
				+ /*", retVal=" + retVal +*/ "]";
	}

}
