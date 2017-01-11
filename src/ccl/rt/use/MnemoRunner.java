package ccl.rt.use;

import io.github.coalangsoft.lib.data.Func;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ccl.rt.Special;
import ccl.rt.Tool;
import ccl.rt.Value;
import ccl.rt.err.Err;
import ccl.rt.store.Variable;
import ccl.rt.vm.Factory;
import ccl.rt.vm.IVM;
import ccl.rt.vm.Runner;
import ccl.rt.Expression;

public class MnemoRunner implements Runner {

	private Func<String, Factory<InputStream>> streamMaker;
	
	private HashMap<String, Integer> marks;
	private ArrayList<String> instructions;
	private ArrayList<String> arguments;
	private Value retVal;
	
	public MnemoRunner(Func<String, Factory<InputStream>> function){
		this.streamMaker = function;
		
		marks = new HashMap<String, Integer>();
		instructions = new ArrayList<String>();
		arguments = new ArrayList<String>();
	}
	
	@Override
	public Value execute(InputStream cclCode, IVM vm) {
		retVal = null;
		
		Scanner s = new Scanner(cclCode);
		int line = 0;
		while(s.hasNextLine()){
			creation(line++, s.nextLine().split(" "));
		}
		s.close();
		for(int i = 0; i < instructions.size();){
			try {
				i = execution(i, instructions.get(i), arguments.get(i), vm);
				if(i == -1){
					return retVal;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return new Expression(Special.UNDEFINED);
	}
	private int execution(int line, String instr, String args, IVM vm) throws Exception {
		switch(instr){
		case "mark": break;
		case "if": return (Boolean) (vm.pop().getValue()) ? marks.get(args) : line+1;
		case "goto": return marks.get(args);
		case "load": vm.load(args); break;
		case "putI": vm.i(args); break;
		case "invoke": invoke(args, vm); break;
		case "store":
			Value v = vm.pop();
			((Variable) vm.pop()).setValue(v);
			break;
		case "store1":
			Variable var = (Variable) vm.pop();
			Value value = vm.pop();
			var.setValue(value);
			break;
		case "newscope": vm.oScope(); break;
		case "oldscope": vm.cScope(); break;
		case "putS": vm.s(args); break;
		case "putA": vm.a(Integer.parseInt(args)); break;
		case "putM": vm.m(this.create(), streamMaker.call(args)); break;
		case "get": vm.put(vm.pop().getProperty(args)); break;
		case "duplicate": vm.dup(); break;
		case "pop":
			Value val = vm.pop();
			if(val instanceof Err){
				return ret(val);
			}else if(val.getValue() instanceof Err){
				return ret((Err) val.getValue());
			}
			break;
		case "ret":
			retVal = vm.pop();
			return -1;
		case "reserve":
			vm.reserve(args);
			break;
		default: throw new RuntimeException("Unknown instr: " + instr);
		}
		return line+1;
	}

	private int ret(Value val) {
		retVal = val;
		return -1;
	}

	private void invoke(String count, IVM vm) throws Exception {
		vm.call(Integer.parseInt(count));
	}

	private void creation(int line, String[] split) {
		instructions.add(split[0]);
		arguments.add(Tool.link(1, split, " "));
		if(split[0].equals("mark")){
			if(marks.get(split[1]) != null){
				throw new RuntimeException("Mark duplication: " + split[1]);
			}else{
				marks.put(split[1], line);
			}
		}
	}

	@Override
	public Runner create() {
		return new MnemoRunner(streamMaker);
	}

}
