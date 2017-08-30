package ccl.rt;

import io.github.coalangsoft.lib.dynamic.DynamicBoolean;
import io.github.coalangsoft.lib.dynamic.DynamicDouble;

import java.util.List;

public class CoaProperty implements IProperty {

    private final String name;
    private final Expression holder;
    private Value property;

    public CoaProperty(Value prop, String name, Expression holder) {
        this.name = name;
        this.holder = holder;
        this.property = prop;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValue(Value n) {
        holder.setProperty(name, n);
        property = n;
    }

    @Override
    public Value getHolder() {
        return holder;
    }

    @Override
    public IProperty getProperty(boolean asPrototype, String name) {
        return property.getProperty(asPrototype,name);
    }

    @Override
    public Object getValue() {
        return property.getValue();
    }

    @Override
    public List<String> getProperties() {
        return property.getProperties();
    }

    @Override
    public Value invoke(Value... args) throws Exception {
        return property.invoke(args);
    }

    @Override
    public DynamicBoolean bool() {
        return property.bool();
    }

    @Override
    public DynamicDouble num() {
        return property.num();
    }

    @Override
    public void setPrototype(Value proto) {
        property.setPrototype(proto);
    }

    @Override
    public String computeType() {
        return property.computeType();
    }
}
