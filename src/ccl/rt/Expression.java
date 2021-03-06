package ccl.rt;

import ccl.jrt.JClassExpression;
import ccl.jrt.JProperty;
import io.github.coalangsoft.lib.data.ConstantFunc;
import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicObject;
import io.github.coalangsoft.lib.log.TimeLogger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ccl.rt.err.Err;
import ccl.rt.lib.Std;
import ccl.rt.vm.IVM;

public class Expression extends DynamicObject<Object> implements Value, Comparable<Expression> {

	private ArrayList<String> propList;
	private HashMap<String, Value> properties;
	private Value prototype;

	private IVM vm;

	protected void setValue(final Object o){
		func = new io.github.coalangsoft.lib.data.Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return o;
			}
		};
	}

	protected void setValueFunc(io.github.coalangsoft.lib.data.Func<Void, Object> val){
		func = val;
	}

	public void setProperty(String name, Value value) {
		if (!propList.contains(name)) {
			propList.add(name);
		}
		properties.put(name, value);
	}

	protected boolean contains(String name) {
		return properties.get(name) != null;
	}

	public static Value make(IVM vm, Object value){
		return CoaCast.cast(vm, value);
	}

	static Expression make0(IVM vm, Object value) {
		Expression e = new Expression(vm, new io.github.coalangsoft.lib.data.Func<Void, Object>() {
			@Override
			public Object call(Void aVoid) {
				return value;
			}
		});

		Field[] fs = value.getClass().getFields();
		for(int i = 0; i < fs.length; i++){
			Field f = fs[i];
			if(!e.getProperties().contains(f.getName())){
				e.getProperties().add(f.getName());
			}
		}

		Method[] ms = value.getClass().getMethods();
		for(int i = 0; i < ms.length; i++){
			Method m = ms[i];
			if(!e.getProperties().contains(m.getName())){
				e.getProperties().add(m.getName());
			}
		}

		return e;
	}

	public Expression(IVM vm, io.github.coalangsoft.lib.data.Func<Void, ? extends Object> value) {
		super(new ConstantFunc(value));

		this.vm = vm;
		this.propList = new ArrayList<String>();
		this.properties = new HashMap<String, Value>();
		initBaseProperties();

		if(vm == null){
			return;
		}
		vm.initPrototype(this);
		if(vm.isDebugState()){
			TimeLogger.std.log("Expression instance created (" + getClass() + ") " + this);
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
		propList.add("push");
		propList.add("native");
	}

	@Override
	public IProperty getProperty(boolean asPrototype, String name) {
		Value v = getProperty0(asPrototype,name);
		if(v instanceof IProperty){
			return (IProperty) v;
		}
		return new CoaProperty(v,name,this);
	}

	protected Value getProperty0(boolean asPrototype, String name) {

		Value v = properties.get(name);
		if (v != null) {
			return v;
		}
		
		if (computeType().equals("number")){
			try{
				double d = Double.parseDouble("0." + name);
				double i = ((Number) Expression.this.getValue()).intValue();
				return Expression.make(vm,d+i);
			}catch(NumberFormatException e){}
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
			case "pow":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return Std.pow(vm, Expression.this, args[0]);
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
				case "format":
					return new Func(vm){
						@Override
						public Value invoke(Value... args) {
							Object[] os = new Object[args.length];
							for(int i = 0; i < os.length; i++){
								os[i] = args[i].getValue();
							}
							return Expression.make(vm,String.format(Expression.this.getValue() + "", os));
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
					return Expression.this.getProperty(false,args[0].getValue() + "");
				}
			};
		case "setProperty":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					Expression.this.setProperty(args[0].getValue() + "", args[1]);
					return Expression.make(vm, Special.UNDEFINED);
				}
			};
		case "type":
			return Expression.make(vm, computeType());
		case "properties":
			return new ArrayValue(vm, Array.clone(vm, propList.toArray(new String[0])));
		case "invoke":
			return new Func(vm){

				@Override
				public Value invoke(Value... args) {
					try {
						return Expression.this.invoke(((Array) args[0].getValue()).toArray());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				
			};
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
		case "equals":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return Std.equals(vm, args[0], Expression.this);
				}
			};
		case "push":
			if(computeType().equals("array")){
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						Array a = (Array) Expression.this.getValue();
						a.pushValue(args[0]);
						return new ArrayValue(vm,a);
					}
				};
			}else{
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return new ArrayValue(vm, new Array(vm, new Value[]{Expression.this,args[0]}));
					}
				};
			}
		case "array":
			return new Func(vm) {
				@Override
				public Value invoke(Value... args) {
					return new ArrayValue(vm, new Array(vm, 
						new io.github.coalangsoft.lib.data.Func<Integer,Value>(){

							@Override
							public Value call(Integer p) {
								// TODO Auto-generated method stub
								try {
									return Expression.this.invoke(Expression.make(vm,p));
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						
						}, args.length >= 1 ? args[0].num().get().intValue() : Integer.MAX_VALUE
					));
				}
			};
			case "native":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						Object value = get();
						return JProperty.get(vm, value, args[0].getValue() + "");
					}
				};
			case "f":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args0) {
						Value argFunc = args0[0];
						return new Func(vm) {
							@Override
							public Value invoke(Value... args1) {
								try {
									return argFunc.invoke(Expression.this.invoke(args1));
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						};
					}
				};
			case "setPrototype":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						Expression.this.setPrototype(args[0]);
						return Expression.make(vm, Special.UNDEFINED);
					}
				};
			case "getPrototype":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						if(prototype == null){
							return Expression.make(vm, Special.UNDEFINED);
						}
						return prototype;
					}
				};
			case "getObjectPrototype":
				return new Func(vm) {
					@Override
					public Value invoke(Value... args) {
						return vm.getPrototype("unknown");
					}
				};

		}

		try{
			Object value = get();
			JProperty prop = JProperty.get(vm, value, name);
			if(!prop.computeType().equals("error")){
				return prop;
			}
		}catch (RuntimeException e){}

		if(prototype != null){
			Value p = prototype.getProperty(true, name);
//			System.err.println("Falling to prototype (" + p + ") of " + this);
			try {
				if(asPrototype && !(this instanceof JProperty || this instanceof JClassExpression)){
					return p;
				}else if(!p.computeType().equals("error")){
					return p.getProperty(false,"bind").invoke(this);
				}
			} catch (Exception e) {}
		}

		return new Err(vm, new Exception("No such property '" + name + "' on object " + this));

	}

	public String computeType() {
		Object value = get();

		if (this instanceof Err || value instanceof Err)
			return "error";
		if (value instanceof Throwable)
			return "error";
		if (value instanceof Boolean){
			return "boolean";
		}
		if (this instanceof JClassExpression)
			return "native";
		if (value instanceof String)
			return "string";
		if (value instanceof Number)
			return "number";
		if (value instanceof Array)
			return "array";
		if (this instanceof JProperty)
			return "native";
		if (this instanceof Func)
			return "function";
		if (value == Special.UNDEFINED || value == null)
			return "undefined";
		return "unknown";
	}

	public Object getValue() {
		try{
			return get();
		}catch(NullPointerException e){
			return Special.UNDEFINED;
		}
	}

	public final List<String> getProperties() {
		return propList;
	}

	public Value invoke(Value... args) throws Exception {
		if (args.length == 0) {
			return this;
		} else if (args.length == 1) {
			return getProperty(false,args[0].getValue() + "");
		} else {
			return new Err(vm, new Exception(
				"Unsupported parameter count: " + args.length
			));
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + " [value=" + get() + ", propList="
				+ propList + ", properties=" + properties + "]";
	}

	@Override
	public int compareTo(Expression o) {
		return ((Comparable) this.getValue()).compareTo(o.getValue());
	}

	public DynamicBoolean bool(){
		return new DynamicBoolean(new io.github.coalangsoft.lib.data.Func<Void, Boolean>() {
			@Override
			public Boolean call(Void aVoid) {
				switch (computeType()){
					case "boolean":
						return (Boolean) get();
					case "string":
						return Boolean.parseBoolean(get().toString());
					case "number":
						return ((Number) get()).intValue() == 1;
					default:
						throw new RuntimeException("Can not convert to boolean: " + get());
				}
			}
		});
	}

	@Override
	public void setPrototype(Value proto) {
		this.prototype = proto;
	}

}
