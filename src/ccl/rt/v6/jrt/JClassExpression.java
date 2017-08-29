package ccl.rt.v6.jrt;

import ccl.csy.CCL;
import ccl.jrt.J;
import ccl.jrt.JInvocationHandler;
import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;
import io.github.coalangsoft.reflect.Clss;

import java.lang.reflect.Proxy;
import java.util.List;

public class JClassExpression extends Expression {

    private final IVM vm;
    private final Clss clss;

    public JClassExpression(IVM vm, Clss clss){
        super(vm, (x) -> clss);
        this.vm = vm;
        this.clss = clss;
    }

    @Override
    public Value getProperty(boolean asPrototype, String name) {
        Clss innerClass = null;

        Clss[] classes = clss.getDeclaredClasses().getRaw();
        for(int i = 0; i < classes.length; i++){
            if(classes[i].getSimpleName().equals(name)){
                innerClass = classes[i];
                break;
            }
        }
        JProperty prop = new JProperty(vm,null, clss.getMethods(null, name), clss.getField(name), innerClass, name);
        return prop;
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
