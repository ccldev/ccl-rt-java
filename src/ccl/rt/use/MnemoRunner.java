package ccl.rt.use;

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
	private boolean executed;
	
	public MnemoRunner(Func<String, Factory<InputStream>> function){
		this.streamMaker = function;
		
		marks = new HashMap<String, Integer>();
		instructions = new ArrayList<String>();
		arguments = new ArrayList<String>();
	}
	
	@Override
	public Value execute(InputStream cclCode, IVM vm) {
		if(executed){
			System.err.println("Warning: a runner should not be executed more then once normally!");
			new RuntimeException("DEBUG, do not panic :)").printStackTrace(System.out);
		}
		executed = true;
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
		case "invoke": invoke(args.split(" "), vm); break;
		case "store":
			Value v = vm.pop();
			((Variable) vm.pop()).setValue(v);
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

	private void invoke(String[] split, IVM vm) throws Exception {
		int params = Integer.parseInt(split[0]);
		if(split.length >= 2){
			throw new RuntimeException("Warning: invoke should be used with 1 parameter!");
		}
		vm.call(params);
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
