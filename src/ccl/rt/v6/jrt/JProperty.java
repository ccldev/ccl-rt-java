package ccl.rt.v6.jrt;

import ccl.jrt.J;
import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.reflect.Clss;
import io.github.coalangsoft.reflect.SpecificMethods;

import java.lang.reflect.Field;

public class JProperty extends Expression {

    private final Field field;
    private final SpecificMethods methods;
    private final Object instance;
    private final Clss innerClass;
    private final IVM vm;

    public JProperty(IVM vm, Object classInstance, SpecificMethods methods, Field field, Clss innerClass) {
        super(vm, (a) -> Special.UNDEFINED);
        this.vm = vm;
        this.instance = classInstance;
        this.methods = methods;
        this.field = field;
        this.innerClass = innerClass;
        initialize();
    }

    private void initialize() {
        if (field == null) {
            setValue(Special.UNDEFINED);
        } else {
            try {
                setValue(field.get(instance));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        "Implementation Exception: Field found but not found :(");
            }
        }
    }

    public void validate(String name) {
        if(field == null && methods.length() == 0 && innerClass == null){
            throw new RuntimeException("No such native property '" + name + "'!");
        }
    }

    @Override
    public Value getProperty(boolean asPrototype, String name) {
        try{
            JProperty prop = JProperty.get(vm, getValue(), name);
            prop.validate(name);
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
            return super.invoke(args);
        }
    }

    public static JProperty get(IVM vm, Object value, String name) {
        if(value == null){
            value = Special.UNDEFINED;
        }
        Clss c = new Clss(value.getClass());
        return new JProperty(vm, value, c.getMethods(value, name), c.getField(name), c.innerClass(name));
    }
}
