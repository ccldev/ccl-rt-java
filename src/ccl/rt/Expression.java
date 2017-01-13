package ccl.rt;

import io.github.coalangsoft.reflect.Clss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import coa.std.NVPValue;

import ccl.jrt.JClass;
import ccl.jrt.JExpression;
import ccl.rt.err.Err;
import ccl.rt.lib.Std;

public class Expression implements Value {
	
	private Object value;

	private ArrayList<String> propList;
	private HashMap<String, Value> properties;

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
	
	public Expression(Object value) {
		setValue(value);
		this.propList = new ArrayList<String>();
		this.properties = new HashMap<String, Value>();
		initBaseProperties();
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
		
		if (computeType().equals("boolean")){
			switch (name){
			case "not":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.not(Expression.this);
					}
				};
			}
		}
		
		if (computeType().equals("number")){
			switch (name) {
			case "lss":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.lss(Expression.this, args[0]);
					}
				};
			case "gtr":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.gtr(Expression.this, args[0]);
					}
				};
			case "add":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.add(Expression.this, args[0]);
					}
				};
			case "sub":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.sub(Expression.this, args[0]);
					}
				};
			case "mul":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.mul(Expression.this, args[0]);
					}
				};
			case "div":
				return new Func() {
					@Override
					public Value invoke(Value... args) {
						return Std.div(Expression.this, args[0]);
					}
				};
			}
		}
		
		if(computeType().equals("string")){
			switch(name){
			case "nvp":
				return new Func() {

					@Override
					public Value invoke(Value... args) {
						return new NVPValue(Expression.this.getValue().toString(), args[0]);
					}
					
				};
			}
		}

		switch (name) {
		case "bind":
			return new Func() {
				@Override
				public Value invoke(Value... args) {
					return Std.bind(Expression.this, args);
				}
			};
		case "unbind":
			return new Func() {
				@Override
				public Value invoke(Value... args) {
					return Std.unbind(Expression.this, args[0]);
				}
			};
		case "for":
			return new Func() {
				@Override
				public Value invoke(Value... args) {
					return Std.for_(Expression.this, args);
				}
			};
		case "while":
			return new Func() {
				@Override
				public Value invoke(Value... args) {
					return Std.while0(Expression.this, args[0]);
				}
			};
		case "property":
			return new Func() {
				@Override
				public Value invoke(Value... args) {
					return Expression.this.getProperty(args[0].getValue() + "");
				}
			};
		case "setProperty":
			return new Func() {
				@Override
				public Value invoke(Value... args) {
					Expression.this.setProperty(args[0].getValue() + "", args[1]);
					return new Expression(Special.UNDEFINED);
				}
			};
		case "type":
			return new Expression(computeType());
		case "properties":
			return new ArrayValue(Array.clone(propList.toArray(new String[0])));
		case "extend":
			return new Func(){

				@Override
				public Value invoke(Value... args) {
					
					final Expression base = Expression.this;
					final Value[] extensions = args;
					return new Func(){

						@Override
						public Value invoke(Value... args) {
							try {
								Value a = base.invoke(args);
								for(int i = 0; i < extensions.length; i++){
									extensions[i].invoke(Tool.link(0, new Value[]{a}, args));
								}
								return a;
							} catch (Exception e) {
								return new Err(e);
							}
							
						}
						
					};
					
				}
				
			};
		}

		return new JExpression(value == null ? Special.UNDEFINED : value, new Clss(value == null ? Special.class : value.getClass()),
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
			return new Err("Unsupported param count: " + args.length);
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + " [value=" + value + ", propList="
				+ propList + ", properties=" + properties + "]";
	}

}
