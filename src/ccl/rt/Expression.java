package ccl.rt;

import io.github.coalangsoft.lib.log.Logger;
import io.github.coalangsoft.reflect.Clss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import coa.std.NVPValue;

import ccl.jrt.JClass;
import ccl.jrt.JExpression;
import ccl.rt.err.Err;
import ccl.rt.lib.Std;
import ccl.rt.vm.IVM;

public class Expression implements Value {
	
	private Object value;

	private ArrayList<String> propList;
	private HashMap<String, Value> properties;

	private IVM vm;

	protected void setProperty(String name, Value value) {
		if (!propList.contains(name)) {
			propList.add(name);
		}
		properties.put(name, value);
	}

	protected void setValue(Object value) {
		if(value instanceof Boolean){
			boolean b = (boolean) value;
			value = b ? 1 : 0;
		}
		this.value = value;
	}

	protected boolean contains(String name) {
		return properties.get(name) != null;
	}
	
	public Expression(IVM vm, Object value) {
		setValue(value);
		this.vm = vm;
		this.propList = new ArrayList<String>();
		this.properties = new HashMap<String, Value>();
		initBaseProperties();
		
		if(vm == null){
			return;
		}
		if(vm.isDebugState()){
			Logger.std.log("Expression instance created (" + getClass() + ") " + this);
		}
	}

	private void initBaseProperties() {
		propList.add("bind");
		propList.add("unbind");
		propList.add("for");
		propList.add("type");
		propList.add("properties");
		propList.add("property");
		propList.add("extend");
	}

	public Value getProperty(String name) {

		Value v = properties.get(name);
		if (v != null) {
			return v;
		}
		
		if (computeType().equals("number")){
			switch (name) {
			case "not":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.not(vm, Expression.this);
					}
				};
			case "lss":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.lss(vm, Expression.this, args[0]);
					}
				};
			case "gtr":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.gtr(vm, Expression.this, args[0]);
					}
				};
			case "add":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.add(vm, Expression.this, args[0]);
					}
				};
			case "sub":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.sub(vm, Expression.this, args[0]);
					}
				};
			case "mul":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.mul(vm, Expression.this, args[0]);
					}
				};
			case "div":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.div(vm, Expression.this, args[0]);
					}
				};
			case "mod":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.mod(vm, Expression.this, args[0]);
					}
				};
			}
		}
		
		if(computeType().equals("string")){
			switch(name){
			case "nvp":
				return new Func(vm) {

					@Override
					public Value invoke(Value... args) {
						return new NVPValue(vm, Expression.this.getValue().toString(), args[0]);
					}
					
				};
			}
		}

		switch (name) {
		case "bind":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return Std.bind(vm, Expression.this, args);
				}
			};
		case "unbind":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return Std.unbind(vm, Expression.this, args[0]);
				}
			};
		case "for":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return Std.for_(vm, Expression.this, args);
				}
			};
		case "while":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return Std.while0(vm, Expression.this, args[0]);
				}
			};
		case "property":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return Expression.this.getProperty(args[0].getValue() + "");
				}
			};
		case "setProperty":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					Expression.this.setProperty(args[0].getValue() + "", args[1]);
					return new Expression(vm, Special.UNDEFINED);
				}
			};
		case "type":
			return new Expression(vm, computeType());
		case "properties":
			return new ArrayValue(vm, Array.clone(vm, propList.toArray(new String[0])));
		case "extend":
			return new Func(vm){

				@Override
				public Value invoke(Value... args) {
					
					final Expression base = Expression.this;
					final Value[] extensions = args;
					return new Func(vm){

						@Override
						public Value invoke(Value... args) {
							try {
								Value a = base.invoke(args);
								for(int i = 0; i < extensions.length; i++){
									extensions[i].invoke(Tool.link(0, new Value[]{a}, args));
								}
								return a;
							} catch (Exception e) {
								return new Err(vm, e);
							}
							
						}
						
					};
					
				}
				
			};
		}

		return new JExpression(vm, value == null ? Special.UNDEFINED : value, new Clss(value == null ? Special.class : value.getClass()),
				name);
	}

	private String computeType() {
		if (this instanceof Err || value instanceof Err)
			return "error";
		if (value instanceof Throwable)
			return "error";
		if (value instanceof Boolean){
			System.err.println("RUNTIME WARNING: Type 'boolean' should not be used!");
			return "boolean";
		}
		if (this instanceof JClass)
			return "native";
		if (value instanceof String)
			return "string";
		if (value instanceof Number)
			return "number";
		if (value instanceof Array)
			return "array";
		if (this instanceof JExpression)
			return "native";
		if (this instanceof Func)
			return "function";
		if (value == Special.UNDEFINED || value == null)
			return "undefined";
		return "unknown";
	}

	public Object getValue() {
		return value;
	}

	public final List<String> getProperties() {
		return propList;
	}

	public Value invoke(Value... args) throws Exception {
		if (args.length == 0) {
			return this;
		} else if (args.length == 1) {
			return getProperty(args[0].getValue() + "");
		} else {
			return new Err(vm, new Exception(
				"Unsupported parameter count: " + args.length
			));
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + " [value=" + value + ", propList="
				+ propList + ", properties=" + properties + "]";
	}

}
