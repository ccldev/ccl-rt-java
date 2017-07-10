package ccl.rt.use;

import ccl.csy.CCL;
import ccl.rt.*;
import ccl.rt.lib.Environment;
import ccl.rt.lib.Spec;
import ccl.rt.lib.Std;
import ccl.rt.vm.StackTraceFormer;
import ccl.v2_1.err.DebugException;
import coa.rt.Nvp;
import coa.rt.NvpVal;
import cpa.subos.io.IOBase;
import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.lib.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Arrays;
import java.util.Scanner;

import ccl.rt.err.Err;
import ccl.rt.store.Scope;
import ccl.rt.store.Variable;
import ccl.rt.vm.IVM;
import ccl.rt.vm.Runner;

//Byte constants
import static ccl.rt.use.InstructionBytes.*;

public class MnemoRunner implements Runner {

	private Scope sc;
	
	private Func<String, Func<Void,IOBase<?>>> streamMaker;
	
	private ArrayList<String> instructions;
	private ArrayList<String> arguments;

	private byte[] instructionBytes;

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

		//translate to bytes
		instructionBytes = new byte[instructions.size()];
		for(int i = 0; i < instructionBytes.length; i++){
			try{
				instructionBytes[i] = InstructionUtil.get(instructions.get(i));
			}catch(NullPointerException e){
				throw new RuntimeException(instructions.get(i), e);
			}
		}
		instructions = null;
	}
	
	@Override
	public Value execute(IVM vm, Scope sc) {
		this.sc = sc;
		if(vm.isDebugState()){
			Logger.std.log("-Execute Start! Runner snapshot: " + this);
		}

		for(int i = 0; i < instructionBytes.length; i++){
			try {
				Value v = execution(instructionBytes[i], arguments.get(i), vm);
				if(v != null){
					return v;
				}
			} catch (Exception e) {
				if(vm.isDebugState()){
					Logger.std.log(">Execution done! Exit state: exception (i:" + i + ")");
				}
				throw new RuntimeException(StackTraceFormer.formException(new Exception("Instruction " + i + " '" + InstructionUtil.get(instructionBytes[i]) + "'",e), vm));
			}
		}
		if(vm.isDebugState()){
			Logger.std.log(">Execution done! Exit state: normal");
		}
		return Expression.make(vm, Special.UNDEFINED);
	}
	
	private Value execution(byte instr, String args, IVM vm) throws Exception{
		switch(instr){
		//For better performance
		case __WHILETRUE: {
			Value func = vm.pop();
			while (true) {
				Value v;
				if ((v = func.invoke()).getValue() != Special.UNDEFINED) {
					return v;
				}
			}
		}
		case __WHILE: {
			Value action = vm.pop();
			Value condition = vm.pop();
			Std.whileGlobal(vm, action, condition);
			break;
		}
		case __FORARR: {
			Value action = vm.pop();
			Array array = (Array) vm.pop().getValue();
			for (int i = 0; i < array.length(); i++) {
				Value v;
				if ((v = action.invoke()).getValue() != Special.UNDEFINED) {
					return v;
				}
			}
			break;
		}
		case __FORNUM: {
			Value action = vm.pop();
			int numto = vm.pop().num().get().intValue();
			int numfrom = vm.pop().num().get().intValue();
			for (int i = numfrom; i < numto + 1; i++) {
				Value v;
				if ((v = action.invoke(Expression.make(vm, i))).getValue() != Special.UNDEFINED) {
					return v;
				}
			}
			break;
		}
		case __WHILETRUE_NB: {
			Value func = vm.pop();
			while (true) {
				func.invoke();
			}
		}
		case __WHILE_NB: {
			Value action = vm.pop();
			Value condition = vm.pop();
			while(condition.invoke().bool().get()){
				action.invoke();
			}
			break;
		}
		case __FORARR_NB: {
			Value action = vm.pop();
			Array array = (Array) vm.pop().getValue();
			for (int i = 0; i < array.length(); i++) {
				action.invoke();
			}
			break;
		}
		case __FORNUM_NB: {
			Value action = vm.pop();
			int numto = vm.pop().num().get().intValue();
			int numfrom = vm.pop().num().get().intValue();
			for (int i = numfrom; i < numto + 1; i++) {
				action.invoke(Expression.make(vm, i));
			}
			break;
		}
		case __PRINTLN:
			System.out.println(vm.pop().getValue());
			break;
		case __PRINTLN_F:
			vm.put(new ccl.rt.Func(vm) {
				@Override
				public Value invoke(Value... args) {
					System.out.println(args[0].getValue());
					return Expression.make(vm, Special.UNDEFINED);
				}
			});
			break;
		case __PRINTLN_C:
			System.out.println(args);
			break;
		case __PRINTLN_CF:
			vm.put(new ccl.rt.Func(vm) {
				@Override
				public Value invoke(Value... vs) {
					System.out.println(args);
					return Expression.make(vm, Special.UNDEFINED);
				}
			});
			break;
		case __SETVAR:
			sc.load(args).setValue(vm.pop());
			break;
		case __MKVAR:
			sc.reserve(args).setValue(vm.pop());
			break;
		case __EXIT:
			System.exit(0);
			break;
		case __THROW:
			return new Err(vm, new Exception(vm.pop().getValue() + ""));
		case __THROW_C:
			return new Err(vm, new Exception(args));
		//"Classic" instructions
		case NNR:
			Value vl = vm.pop();
			if(vl != null){
				if(vl.getValue() != Special.UNDEFINED){
					return vl;
				}
			}
			break;
		case NNR2:
			Value v = vm.pop();
			if(v != null){
				if(v.getValue() != Special.UNDEFINED){
					return v;
				}else{
					vm.put(v);
				}
			}
			break;
		case LOAD: vm.load(args, sc); break;
		case PUTI: vm.i(args); break;
		case INVOKE: invoke(args, vm); break;
		case INVOKE1: invoke1(args, vm); break;
		case STORE1:
			Variable var = (Variable) vm.pop();
			Value value = vm.pop();
			var.setValue(value);
			break;
		case PUTS: vm.s(args); break;
		case PUTA: vm.a(); break;
		case PUTM: vm.m(this.create(), streamMaker.call(args), sc); break;
		case GET: vm.put(vm.pop().getProperty(args)); break;
		case DUPLICATE: vm.dup(); break;
		case POP:
			Value val = vm.pop();

			if(val instanceof Err){
				return val;
			}else if(val.getValue() instanceof Err){
				return (Err) val.getValue();
			}
			break;
		case RET:
			Value ret = vm.pop();
			return ret;
		case RESERVE:
			vm.reserve(args, sc);
			break;
		case __UNDEFINED:
			vm.put(Expression.make(vm, Special.UNDEFINED));
			break;
		case __MKVAR_U:
			sc.reserve(args);
			break;
		case __JAVA:
			vm.put(Spec.java(vm, CCL.classFinder, args));
			break;
		case __INVOKE0:
			vm.put(vm.pop().invoke());
			break;
		case __INVOKE1: {
			Value param = vm.pop();
			if(param instanceof Nvp){
				Value res = vm.pop().invoke();
				param.invoke(res);
				vm.put(res);
			}else{
				vm.put(vm.pop().invoke(param));
			}
			break;
		}
		case __INVOKE2: {
			Value param2 = vm.pop();
			Value param1 = vm.pop();
			if(param1 instanceof Nvp){
				if(param2 instanceof Nvp){
					Value res = vm.pop().invoke();
					param1.invoke(res);
					param2.invoke(res);
					vm.put(res);
				}else{
					Value res = vm.pop().invoke(param1);
					param2.invoke(res);
					vm.put(res);
				}
			}else if(param2 instanceof Nvp){
				Value res = vm.pop().invoke(param1);
				param2.invoke(res);
				vm.put(res);
			}else{
				vm.put(vm.pop().invoke(param1, param2));
			}
			break;
		}
		case __ARRPUSH1: {
			Value toPush = vm.pop();
			Value arr = vm.pop();
			((Array) arr.getValue()).pushValue(toPush);
			vm.put(arr);
			break;
		}
		case __ARRPUSH2: {
			Value toPush = vm.pop();
			Expression arr = (Expression) vm.pop();
			((Array) arr.getValue()).pushValue(toPush);
			arr.setProperty(args, toPush);
			vm.put(arr);
			break;
		}
		case __FLOAT: {
			vm.put(Expression.make(vm, Double.parseDouble(args)));
			break;
		}
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
