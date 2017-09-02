package ccl.jrt;

import ccl.rt.Expression;
import ccl.rt.IProperty;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.reflect.Clss;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JClassExpression extends Expression {

    private final IVM vm;
    private final Clss clss;

    public JClassExpression(IVM vm, Clss clss){
        super(vm, (x) -> clss);
        this.vm = vm;
        this.clss = clss;

        Field[] fs = clss.base.getFields();
        for(int i = 0; i < fs.length; i++){
            Field f = fs[i];
            if(Modifier.isStatic(f.getModifiers())){
                if(!getProperties().contains(f.getName())){
                    getProperties().add(f.getName());
                }
            }
        }

        Method[] ms = clss.base.getMethods();
        for(int i = 0; i < ms.length; i++){
            Method m = ms[i];
            if(Modifier.isStatic(m.getModifiers())){
                if(!getProperties().contains(m.getName())){
                    getProperties().add(m.getName());
                }
            }
        }
    }

    @Override
    public IProperty getProperty(boolean asPrototype, String name) {
        Clss innerClass = null;

        Clss[] classes = clss.getDeclaredClasses().getRaw();
        for(int i = 0; i < classes.length; i++){
            if(classes[i].getSimpleName().equals(name)){
                innerClass = classes[i];
                break;
            }
        }
        try{
            JProperty prop = new JProperty(vm,null, clss.getMethods(null, name), clss.getField(name), innerClass, name);
            if(prop.computeType().equals("error")){
                return super.getProperty(asPrototype,name);
            }
            return prop;
        }catch(Exception e){
            return super.getProperty(asPrototype,name);
        }
    }

    @Override
    public Value invoke(Value... args) throws Exception {
        return J.invokeClass(vm, (Clss) getValue(), args);
    }

//    @Override
//    public void setPrototype(Value proto) {
//
//    }
//
//    @Override
//    public String computeType() {
//        return null;
//    }
}
