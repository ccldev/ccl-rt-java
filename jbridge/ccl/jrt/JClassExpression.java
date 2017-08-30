package ccl.jrt;

import ccl.rt.Expression;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import io.github.coalangsoft.reflect.Clss;

public class JClassExpression extends Expression {

    private final IVM vm;
    private final Clss clss;

    public JClassExpression(IVM vm, Clss clss){
        super(vm, (x) -> clss);
        this.vm = vm;
        this.clss = clss;
    }

    @Override
    public JProperty getProperty(boolean asPrototype, String name) {
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
