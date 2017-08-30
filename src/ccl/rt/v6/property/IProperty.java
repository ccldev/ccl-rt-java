package ccl.rt.v6.property;

import ccl.rt.Value;

public interface IProperty extends Value{

    String getName();
    void setValue(Value n);
    Value getHolder();

}
