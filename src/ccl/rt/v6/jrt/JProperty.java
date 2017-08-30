package ccl.rt.v6.jrt;

import ccl.jrt.J;
import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.v6.property.IProperty;
import ccl.rt.vm.IVM;
import coa.rt.Nvp;
import io.github.coalangsoft.lib.data.Func;
import io.github.coalangsoft.reflect.Clss;
import io.github.coalangsoft.reflect.SpecificMethods;

import java.lang.reflect.Field;

public class JProperty extends Expression implements IProperty {

    private Func<Value,Void> setter;
    private Func<Void,Object> getter;
    private final SpecificMethods methods;
    private final Object instance;
    private final Clss innerClass;
    private final IVM vm;
    private final String name;

    public JProperty(IVM vm, Object classInstance, SpecificMethods methods, Field field, Clss innerClass, String name) {
        super(vm, (a) -> Special.UNDEFINED);
        this.vm = vm;
        this.instance = classInstance;
        this.methods = methods;
        if(field != null){
            this.setter = new JFieldSetter(vm,instance,field);
            this.getter = new JFieldGetter(instance,field);
        }
        this.innerClass = innerClass;
        this.name = name;
        initialize();
        validate();
    }

    private void initialize() {
        if(getter == null && instance != null){
            //get value by method
            Clss c = new Clss(instance.getClass());
            SpecificMethods set = c.getMethods(instance, Nvp.makeMethodName("set", name));
            if(set.length() != 0){
                setter = new JMethodSetter(vm, instance, set);
            }

            SpecificMethods get = c.getMethods(instance, Nvp.makeMethodName("get", name));
            if(get.length() == 0){
                get = c.getMethods(instance, Nvp.makeMethodName("is", name));
            }if(get.length() != 0){
                getter = new JMethodGetter(get);
            }

        }
        setValueFunc(getter);
    }

    private void validate() {
        if(getter == null && methods.length() == 0 && innerClass == null){
            throw new RuntimeException("No such native property '" + name + "'!");
        }
    }

    @Override
    public IProperty getProperty(boolean asPrototype, String name) {
        try{
            JProperty prop = JProperty.get(vm, getValue(), name);
            prop.validate();
            return prop;
        }catch (RuntimeException e){
            return super.getProperty(asPrototype, name);
        }
//        throw new RuntimeException("NIy");
    }

    @Override
    public Value invoke(Value... args) throws Exception {
        if(methods.length() != 0){
            return J.invoke(vm, instance, methods, args);
        }else if(innerClass != null){
            return J.invoke(vm, instance, innerClass, args);
        }else{
            new RuntimeException("Trying to invoke " + this).printStackTrace();
            try{
                return super.invoke(args);
            }catch (RuntimeException e){
                throw new RuntimeException("Unable to invoke " + this, e);
            }
        }
    }

    public static JProperty get(IVM vm, Object value, String name) {
        if(value == null){
            value = Special.UNDEFINED;
        }
        Clss c = new Clss(value.getClass());
        return new JProperty(vm, value, c.getMethods(value, name), c.getField(name), c.innerClass(name), name);
    }

    @Override
    public String toString() {
        return "JProperty{" +
                "name=" + name +
                ", setter=" + setter +
                ", getter=" + getter +
                ", methods=" + methods +
                ", instance=" + instance +
                ", innerClass=" + innerClass +
                ", vm=" + vm +
                '}';
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValue(Value n) {
        setter.call(n);
    }

    @Override
    public Value getHolder() {
        throw new RuntimeException("NIy");
    }

    public String computeType(){
        if(getter != null && setter != null){
            return super.computeType();
        }else if(methods != null){
            return "function";
        }else{
            return "error";
        }
    }
}
